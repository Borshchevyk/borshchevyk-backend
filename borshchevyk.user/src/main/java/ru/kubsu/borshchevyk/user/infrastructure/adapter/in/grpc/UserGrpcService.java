package ru.kubsu.borshchevyk.user.infrastructure.adapter.in.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.kubsu.borshchevyk.grpc.SearchUsersRequest;
import ru.kubsu.borshchevyk.grpc.UserRequest;
import ru.kubsu.borshchevyk.grpc.UserResponse;
import ru.kubsu.borshchevyk.grpc.UserServiceGrpc;
import ru.kubsu.borshchevyk.grpc.UsersBatchRequest;
import ru.kubsu.borshchevyk.grpc.UsersBatchResponse;
import ru.kubsu.borshchevyk.user.application.dto.command.GetUsersBatchCommand;
import ru.kubsu.borshchevyk.user.application.dto.command.SearchUsersCommand;
import ru.kubsu.borshchevyk.user.application.port.in.GetUserProfileUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.GetUsersBatchUseCase;
import ru.kubsu.borshchevyk.user.application.port.in.SearchUsersUseCase;
import ru.kubsu.borshchevyk.user.application.dto.command.GetUserProfileCommand;
import ru.kubsu.borshchevyk.user.domain.model.user.User;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * gRPC service for User operations.
 *
 * @author Aleksey Timko
 * @since 2026-03-15
 */
@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final GetUsersBatchUseCase getUsersBatchUseCase;
    private final SearchUsersUseCase searchUsersUseCase;

    @Override
    public void searchUsers(SearchUsersRequest request, StreamObserver<UsersBatchResponse> responseObserver) {
        try {
            List<User> users = searchUsersUseCase.searchUsers(
                    SearchUsersCommand.builder()
                            .query(request.getQuery())
                            .requesterId(request.getRequesterId().isEmpty() ? null : request.getRequesterId())
                            .build()
            );

            UsersBatchResponse response = UsersBatchResponse.newBuilder()
                    .addAllUsers(users.stream()
                            .map(user -> UserResponse.newBuilder()
                                    .setUserId(user.getUserId().getValue().toString())
                                    .setFirstName(user.getFirstName() != null ? user.getFirstName() : "")
                                    .setLastName(user.getLastName() != null ? user.getLastName() : "")
                                    .setTag(user.getTag().getValue())
                                    .setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to search users: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserInfo(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            User user = getUserProfileUseCase.getUserProfile(GetUserProfileCommand.builder()
                    .targetUserIdOrTag(request.getUserId())
                    .build());
            
            UserResponse response = UserResponse.newBuilder()
                    .setUserId(user.getUserId().getValue().toString())
                    .setFirstName(user.getFirstName() != null ? user.getFirstName() : "")
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
    public void getUsersBatch(UsersBatchRequest request, StreamObserver<UsersBatchResponse> responseObserver) {
        try {
            List<UUID> uuids = request.getUserIdsList().stream()
                    .map(UUID::fromString)
                    .toList();
            
            List<User> users = getUsersBatchUseCase.getUsersBatch(
                    GetUsersBatchCommand.builder()
                            .userIds(uuids)
                            .build()
            );

            UsersBatchResponse response = UsersBatchResponse.newBuilder()
                    .addAllUsers(users.stream()
                            .map(user -> UserResponse.newBuilder()
                                    .setUserId(user.getUserId().getValue().toString())
                                    .setFirstName(user.getFirstName())
                                    .setLastName(user.getLastName() != null ? user.getLastName() : "")
                                    .setTag(user.getTag().getValue())
                                    .setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "")
                                    .build())
                            .collect(Collectors.toList()))
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
