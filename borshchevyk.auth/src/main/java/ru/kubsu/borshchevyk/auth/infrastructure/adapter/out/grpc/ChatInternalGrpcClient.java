package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.grpc.ChatInternalServiceGrpc;
import ru.kubsu.borshchevyk.grpc.CreateSavedMessagesRequest;
import ru.kubsu.borshchevyk.grpc.CreateSavedMessagesResponse;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatInternalGrpcClient {

    @GrpcClient("message-service")
    private ChatInternalServiceGrpc.ChatInternalServiceBlockingStub chatInternalServiceStub;

    public void createSavedMessages(UUID userId) {
        log.info("Sending gRPC request to create saved messages for user: {}", userId);
        try {
            CreateSavedMessagesRequest request = CreateSavedMessagesRequest.newBuilder()
                    .setUserId(userId.toString())
                    .build();
            CreateSavedMessagesResponse response = chatInternalServiceStub.createSavedMessages(request);
            if (response.getSuccess()) {
                log.info("Successfully created saved messages for user: {}, chatId: {}", userId, response.getChatId());
            } else {
                log.error("Failed to create saved messages for user: {}. Reason: {}", userId, response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Error while calling message-service to create saved messages for user: {}", userId, e);
        }
    }
}
