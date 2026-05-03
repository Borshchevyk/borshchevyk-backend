package ru.kubsu.borshchevyk.calls.application.port.in;

/**
 * Use case for handling LiveKit webhooks.
 */
public interface HandleLiveKitWebhookUseCase {
    /**
     * Handles the event when a LiveKit room is finished.
         * @param roomId the unique room ID
     */
    void handleRoomFinished(String roomId);

    /**
     * Handles the event when a participant joins a LiveKit room.
         * @param roomId the unique room ID
     * @param participantIdentity the identity of the joined participant
     */
    void handleParticipantJoined(String roomId, String participantIdentity);

    /**
     * Handles the event when a participant leaves a LiveKit room.
         * @param roomId the unique room ID
     * @param participantIdentity the identity of the left participant
     */
    void handleParticipantLeft(String roomId, String participantIdentity);
}
