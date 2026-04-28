package ru.kubsu.borshchevyk.calls.application.port.in;

/**
 * Use case for handling LiveKit webhooks.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public interface HandleLiveKitWebhookUseCase {
    void handleRoomFinished(String roomId);
    void handleParticipantJoined(String roomId, String participantIdentity);
    void handleParticipantLeft(String roomId, String participantIdentity);
}
