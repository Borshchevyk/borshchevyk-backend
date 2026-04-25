package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.grpc.UserRequest;
import ru.kubsu.borshchevyk.grpc.UserResponse;
import ru.kubsu.borshchevyk.grpc.UserServiceGrpc;

import java.util.List;
import java.util.UUID;

/**
 * gRPC client for fetching user info from user-service.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
@Service
@RequiredArgsConstructor
public class UserGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public UserResponse getUserInfo(UUID userId) {
        UserRequest request = UserRequest.newBuilder()
                .setUserId(userId.toString())
                .build();
        return userServiceStub.getUserInfo(request);
    }

    public List<UserResponse> getUsersBatch(List<UUID> userIds) {
        ru.kubsu.borshchevyk.grpc.UsersBatchRequest request = ru.kubsu.borshchevyk.grpc.UsersBatchRequest.newBuilder()
                .addAllUserIds(userIds.stream().map(UUID::toString).toList())
                .build();
        return userServiceStub.getUsersBatch(request).getUsersList();
    }
}
