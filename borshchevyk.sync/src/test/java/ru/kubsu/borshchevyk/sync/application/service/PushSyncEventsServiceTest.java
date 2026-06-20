package ru.kubsu.borshchevyk.sync.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kubsu.borshchevyk.sync.application.dto.command.PushSyncEventsCommand;
import ru.kubsu.borshchevyk.sync.application.port.out.BroadcastSyncEventPort;
import ru.kubsu.borshchevyk.sync.application.port.out.LoadSyncEventsPort;
import ru.kubsu.borshchevyk.sync.application.port.out.PublishSyncMutationPort;
import ru.kubsu.borshchevyk.sync.application.port.out.SaveSyncEventPort;
import ru.kubsu.borshchevyk.sync.domain.model.EventType;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.domain.model.VectorClock;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PushSyncEventsServiceTest {

    @Mock
    private LoadSyncEventsPort loadSyncEventsPort;

    @Mock
    private SaveSyncEventPort saveSyncEventPort;

    @Mock
    private BroadcastSyncEventPort broadcastSyncEventPort;

    @Mock
    private PublishSyncMutationPort publishSyncMutationPort;

    @InjectMocks
    private PushSyncEventsService pushSyncEventsService;

    @Captor
    private ArgumentCaptor<SyncEvent> syncEventCaptor;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldPushEventsAndMergeVectorClock() {
        // Arrange
        // Server's current state: Server=1, ClientA=1, ClientB=5
        VectorClock currentServerClock = new VectorClock(Map.of("server", 1L, "ClientA", 1L, "ClientB", 5L));
        when(loadSyncEventsPort.loadAndLockCurrentServerClock()).thenReturn(currentServerClock);

        // Client pushes an event claiming they did something after receiving server state 1
        // Client state: Server=1, ClientA=2
        VectorClock clientClock = new VectorClock(Map.of("server", 1L, "ClientA", 2L));
        SyncEvent clientEvent = new SyncEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                EventType.MESSAGE_CREATED,
                "{}",
                clientClock,
                Instant.now()
        );

        PushSyncEventsCommand command = new PushSyncEventsCommand(List.of(clientEvent));

        // Act
        VectorClock resultingClock = pushSyncEventsService.push(command);

        // Assert
        // The resulting clock should be merged and incremented by the server:
        // Merge: Max(Server:1, Server:1) = 1, Max(ClientA:1, ClientA:2) = 2, Max(ClientB:5, ClientB:0) = 5 -> {Server:1, ClientA:2, ClientB:5}
        // Increment server: {Server:2, ClientA:2, ClientB:5}
        
        assertEquals(2L, resultingClock.getClocks().get("server"));
        assertEquals(2L, resultingClock.getClocks().get("ClientA"));
        assertEquals(5L, resultingClock.getClocks().get("ClientB"));

        verify(saveSyncEventPort).save(syncEventCaptor.capture());
        verify(broadcastSyncEventPort).broadcast(syncEventCaptor.capture());

        SyncEvent savedEvent = syncEventCaptor.getAllValues().get(0);
        
        // The saved event's clock should retain the client's original components, plus the updated server clock component
        VectorClock expectedSavedClock = new VectorClock(Map.of("server", 2L, "ClientA", 2L));
        assertEquals(expectedSavedClock, savedEvent.vectorClock());
    }
}
