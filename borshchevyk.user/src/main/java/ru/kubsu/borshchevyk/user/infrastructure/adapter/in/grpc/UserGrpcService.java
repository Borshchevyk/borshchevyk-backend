package ru.kubsu.borshchevyk.user.infrastructure.adapter.`in`.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.kubsu.borshchevyk.grpc.UserRequest;
import ru.kubsu.borshchevyk.grpc.UserResponse;
import ru.kubsu.borshchevyk.grpc.UserServiceGrpc;
import ru.kubsu.borshchevyk.user.application.port.`in`.GetUserProfileUseCase;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final GetUserProfileUseCase getUserProfileUseCase;

    private final ru.kubsu.borshchevyk.user.application.port.`in`.GetUsersBatchUseCase getUsersBatchUseCase;

    @Override
    public void getUserInfo(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            User user = getUserProfileUseCase.getUserProfile(request.getUserId());
            
            UserResponse response = UserResponse.newBuilder()
                    .setUserId(user.getUserId().value().toString())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName() != null ? user.getLastName() : "")
                    .setTag(user.getTag().getValue())
                    .setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("User not found: " + request.getUserId())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUsersBatch(ru.kubsu.borshchevyk.grpc.UsersBatchRequest request, StreamObserver<ru.kubsu.borshchevyk.grpc.UsersBatchResponse> responseObserver) {
        try {
            java.util.List<java.util.UUID> uuids = request.getUserIdsList().stream()
                    .map(java.util.UUID::fromString)
                    .toList();
            
            java.util.List<User> users = getUsersBatchUseCase.getUsersBatch(
                    ru.kubsu.borshchevyk.user.application.dto.command.GetUsersBatchCommand.builder()
                            .userIds(uuids)
                            .build()
            );

            ru.kubsu.borshchevyk.grpc.UsersBatchResponse response = ru.kubsu.borshchevyk.grpc.UsersBatchResponse.newBuilder()
                    .addAllUsers(users.stream()
                            .map(user -> UserResponse.newBuilder()
                                    .setUserId(user.getUserId().value().toString())
                                    .setFirstName(user.getFirstName())
                                    .setLastName(user.getLastName() != null ? user.getLastName() : "")
                                    .setTag(user.getTag().getValue())
                                    .setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                                    .build())
                            .toList())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to fetch users batch: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
