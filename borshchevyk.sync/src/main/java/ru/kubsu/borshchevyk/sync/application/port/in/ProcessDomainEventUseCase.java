package ru.kubsu.borshchevyk.sync.application.port.in;

import ru.kubsu.borshchevyk.sync.application.dto.command.ProcessDomainEventCommand;

/**
 * Generic use case for processing incoming domain events from other microservices.
 *
 * @author Aleksey Timko
 */
public interface ProcessDomainEventUseCase {
    void process(ProcessDomainEventCommand command);
}
