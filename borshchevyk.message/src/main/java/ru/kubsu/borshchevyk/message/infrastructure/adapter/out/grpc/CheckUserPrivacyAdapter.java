package ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.CheckUserPrivacyPort;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CheckUserPrivacyAdapter implements CheckUserPrivacyPort {

    private final UserGrpcClient userGrpcClient;

    @Override
    public boolean canInviteToChat(UUID targetUserId, UUID requesterId) {
        return !userGrpcClient.checkInvitePermission(targetUserId, requesterId);
    }
}