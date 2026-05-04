package ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.calls.application.port.out.GetUserInfoPort;
import ru.kubsu.borshchevyk.calls.domain.model.User;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.grpc.mapper.UserMapper;
import ru.kubsu.borshchevyk.grpc.UserRequest;
import ru.kubsu.borshchevyk.grpc.UserResponse;
import ru.kubsu.borshchevyk.grpc.UserServiceGrpc;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetUserInfoGrpcClient implements GetUserInfoPort {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    private final UserMapper userMapper;

    public User getUserInfo(UUID userId) {
        UserRequest request = UserRequest.newBuilder()
                .setUserId(userId.toString())
                .build();
        UserResponse response = userServiceStub.getUserInfo(request);
        return userMapper.toDomain(response);
    }
}
