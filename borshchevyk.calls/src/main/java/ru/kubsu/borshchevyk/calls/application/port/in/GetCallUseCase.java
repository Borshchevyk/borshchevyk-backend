package ru.kubsu.borshchevyk.calls.application.port.in;

import jakarta.validation.Valid;
import ru.kubsu.borshchevyk.calls.application.dto.query.GetCallQuery;
import ru.kubsu.borshchevyk.calls.domain.model.Call;

public interface GetCallUseCase {
    /**
     * Retrieves details of a specific call.
     *
     * @param query the get call query
     * @return the Call domain object
     */
    Call getCall(@Valid GetCallQuery query);
}
