package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.sync.application.port.out.BroadcastSyncEventPort;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response.SyncEventDto;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.mapper.SyncWebMapper;

/**
 * Adapter for broadcasting synchronization events via WebSocket using Spring's SimpMessagingTemplate.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSyncEventBroadcastAdapter implements BroadcastSyncEventPort {

    private final SimpMessagingTemplate messagingTemplate;
    private final SyncWebMapper syncWebMapper;

    private static final String BROADCAST_DESTINATION = "/topic/sync";

    @Override
    public void broadcast(SyncEvent event) {
        log.debug("Broadcasting sync event to {} via WebSocket: id={}, vectorClock={}", 
                BROADCAST_DESTINATION, event.id(), event.vectorClock());
        
        SyncEventDto dto = syncWebMapper.toDto(event);
        messagingTemplate.convertAndSend(BROADCAST_DESTINATION, dto);
    }
}
