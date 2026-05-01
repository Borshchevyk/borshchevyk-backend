package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.kubsu.borshchevyk.grpc.ChatInternalServiceGrpc;
import ru.kubsu.borshchevyk.grpc.CreateSavedMessagesRequest;
import ru.kubsu.borshchevyk.grpc.CreateSavedMessagesResponse;
import ru.kubsu.borshchevyk.message.application.dto.command.CreateChatCommand;
import ru.kubsu.borshchevyk.message.application.port.in.CreateChatUseCase;
import ru.kubsu.borshchevyk.message.domain.model.chat.Chat;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatType;

import java.util.List;
import java.util.UUID;

/**
 * @author Aleksey Timko
 * @since 2026-05-01
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ChatInternalGrpcService extends ChatInternalServiceGrpc.ChatInternalServiceImplBase {

    private final CreateChatUseCase createChatUseCase;

    @Override
    public void createSavedMessages(CreateSavedMessagesRequest request, StreamObserver<CreateSavedMessagesResponse> responseObserver) {
        log.info("Received gRPC request to create saved messages for user: {}", request.getUserId());
        try {
            UUID userId = UUID.fromString(request.getUserId());
            CreateChatCommand command = CreateChatCommand.builder()
                    .creatorId(userId)
                    .type(ChatType.SAVED_MESSAGES)
                    .title("Saved Messages")
                    .description("Your personal saved messages")
                    .initialMemberIds(List.of())
                    .build();

            Chat chat = createChatUseCase.createChat(command);

            CreateSavedMessagesResponse response = CreateSavedMessagesResponse.newBuilder()
                    .setSuccess(true)
                    .setChatId(chat.getId().value().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("Successfully created saved messages via gRPC for user: {}", userId);
        } catch (Exception e) {
            log.error("Error creating saved messages via gRPC for user: {}", request.getUserId(), e);
            CreateSavedMessagesResponse response = CreateSavedMessagesResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage() != null ? e.getMessage() : "Unknown error")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
