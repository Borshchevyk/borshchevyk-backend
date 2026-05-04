package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.GetUsersBatchPort;
import ru.kubsu.borshchevyk.calls.domain.model.User;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.grpc.mapper.GrpcUserMapper;
import ru.kubsu.borshchevyk.grpc.UserResponse;
import ru.kubsu.borshchevyk.grpc.UserServiceGrpc;
import ru.kubsu.borshchevyk.grpc.UsersBatchRequest;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetUsersBatchGrpcClient implements GetUsersBatchPort {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    private final GrpcUserMapper grpcUserMapper;

    public List<User> getUsersBatch(List<UUID> userIds) {
        UsersBatchRequest request = UsersBatchRequest.newBuilder()
                .addAllUserIds(userIds.stream().map(UUID::toString).toList())
                .build();
        List<UserResponse> userResponses = userServiceStub.getUsersBatch(request).getUsersList();
        return userResponses.stream().map(grpcUserMapper::toDomain).toList();
    }
}
