package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.livekit;

import io.livekit.server.AccessToken;
import io.livekit.server.CanPublish;
import io.livekit.server.CanSubscribe;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.LiveKitPort;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;

/**
 * Adapter for generating LiveKit JWT tokens.
 */
@Component
@Slf4j
public class LiveKitAdapter implements LiveKitPort {

    @Value("${app.livekit.api-key}")
    private String apiKey;

    @Value("${app.livekit.api-secret}")
    private String apiSecret;

    @Override
    public String generateJoinToken(String roomId, UserId userId, boolean canPublish) {
        log.debug("Generating LiveKit token for user {} in room {}", userId.value(), roomId);
        
        AccessToken token = new AccessToken(apiKey, apiSecret);

        token.setName(userId.value().toString());
        token.setIdentity(userId.value().toString());

        token.addGrants(
                new RoomJoin(true),
                new RoomName(roomId),
                new CanPublish(canPublish),
                new CanSubscribe(true)
        );
        
        return token.toJwt();
    }
}
