package ru.kubsu.borshchevyk.calls.application.port.in;

import ru.kubsu.borshchevyk.calls.application.dto.command.EndCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.InitiateCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.JoinCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.command.LeaveCallCommand;
import ru.kubsu.borshchevyk.calls.application.dto.query.GetCallQuery;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

/**
 * Inbound port for managing call lifecycles.
 *
 * @author Aleksey Timko
 * @since 2026-04-25
 */
public interface ManageCallUseCase {
    /**
     * Initiates a new call with the given participants.
     *
     * @param command the initiation command
     * @return the created Call domain object
     */
    Call initiateCall(InitiateCallCommand command);

    /**
     * Generates a token for a user to join an active call.
     *
     * @param command the join command
     * @return LiveKit JWT access token
     */
    String joinCall(JoinCallCommand command);

    /**
     * Terminates an active call.
     *
     * @param command the end call command
     * @return the updated Call domain object
     */
    Call endCall(EndCallCommand command);

    /**
     * Handles a user leaving the call.
     *
     * @param command the leave command
     * @return the updated Call domain object
     */
    Call leaveCall(LeaveCallCommand command);

    /**
     * Retrieves details of a specific call.
     *
     * @param query the get call query
     * @return the Call domain object
     */
    Call getCall(GetCallQuery query);
}
