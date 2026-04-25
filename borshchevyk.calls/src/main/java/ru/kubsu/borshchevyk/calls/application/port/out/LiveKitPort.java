package ru.kubsu.borshchevyk.calls.application.port.out;

import ru.kubsu.borshchevyk.calls.domain.model.Call;
import ru.kubsu.borshchevyk.calls.domain.model.UserId;

/**
 * Port for interacting with the LiveKit server.
 *
 * @author Gemini
 * @since 2026-04-25
 */
public interface LiveKitPort {
    /**
     * Generates a LiveKit JWT token for a user to join a specific room.
     */
    String generateJoinToken(String roomId, UserId userId, boolean canPublish);
}
