package ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.grpc.UserRequest;
import ru.kubsu.borshchevyk.grpc.UserResponse;
import ru.kubsu.borshchevyk.grpc.UserServiceGrpc;

import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
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

    public java.util.List<UserResponse> getUsersBatch(java.util.List<UUID> userIds) {
        ru.kubsu.borshchevyk.grpc.UsersBatchRequest request = ru.kubsu.borshchevyk.grpc.UsersBatchRequest.newBuilder()
                .addAllUserIds(userIds.stream().map(UUID::toString).toList())
                .build();
        return userServiceStub.getUsersBatch(request).getUsersList();
    }

    public java.util.List<UserResponse> searchUsers(String query, UUID requesterId) {
        ru.kubsu.borshchevyk.grpc.SearchUsersRequest request = ru.kubsu.borshchevyk.grpc.SearchUsersRequest.newBuilder()
                .setQuery(query)
                .setRequesterId(requesterId != null ? requesterId.toString() : "")
                .build();
        return userServiceStub.searchUsers(request).getUsersList();
    }
}
