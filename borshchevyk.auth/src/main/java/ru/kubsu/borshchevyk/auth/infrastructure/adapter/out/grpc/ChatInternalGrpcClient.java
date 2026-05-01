package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.auth.application.port.out.CreateSavedMessagesPort;
import ru.kubsu.borshchevyk.auth.domain.model.value.AccountId;
import ru.kubsu.borshchevyk.grpc.ChatInternalServiceGrpc;
import ru.kubsu.borshchevyk.grpc.CreateSavedMessagesRequest;
import ru.kubsu.borshchevyk.grpc.CreateSavedMessagesResponse;

/**
 * Adapter for communicating with the Message microservice via gRPC.
 * Responsible for operations like creating saved messages chat for users.
 *
 * @author Aleksey Timko
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatInternalGrpcClient implements CreateSavedMessagesPort {

    @GrpcClient("message-service")
    private ChatInternalServiceGrpc.ChatInternalServiceBlockingStub chatInternalServiceStub;

    /**
     * Creates a saved messages chat for the specified user account ID.
     *
     * @param accountId the account ID of the user
     */
    @Override
    public void createSavedMessages(AccountId accountId) {
        log.info("Sending gRPC request to create saved messages for user: {}", accountId.value());
        try {
            CreateSavedMessagesRequest request = CreateSavedMessagesRequest.newBuilder()
                    .setUserId(accountId.value().toString())
                    .build();
            CreateSavedMessagesResponse response = chatInternalServiceStub.createSavedMessages(request);
            if (response.getSuccess()) {
                log.info("Successfully created saved messages for user: {}, chatId: {}", accountId.value(), response.getChatId());
            } else {
                log.error("Failed to create saved messages for user: {}. Reason: {}", accountId.value(), response.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("Error while calling message-service to create saved messages for user: {}", accountId.value(), e);
        }
    }
}
