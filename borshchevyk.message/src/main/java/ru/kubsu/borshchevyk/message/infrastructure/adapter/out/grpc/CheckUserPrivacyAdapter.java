package ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.CheckUserPrivacyPort;

import java.util.UUID;

/**
 * Adapter for checking user privacy settings via gRPC.
 *
 * @author Aleksey Timko
 */
@Component
@RequiredArgsConstructor
public class CheckUserPrivacyAdapter implements CheckUserPrivacyPort {

    private final UserGrpcClient userGrpcClient;

    @Override
    public boolean canInviteToChat(UUID targetUserId, UUID requesterId) {
        return userGrpcClient.checkInvitePermission(targetUserId, requesterId);
    }
}
