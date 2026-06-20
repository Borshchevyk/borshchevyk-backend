package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response.SyncEventDto;

import java.util.List;

/**
 * Mapper for web layer DTOs in the sync service.
 *
 * @author Aleksey Timko
 */
@Mapper(componentModel = "spring")
public interface SyncWebMapper {

    SyncEventDto toDto(SyncEvent event);

    List<SyncEventDto> toDtoList(List<SyncEvent> events);

    SyncEvent toDomain(SyncEventDto dto);

    List<SyncEvent> toDomainList(List<SyncEventDto> dtos);
}
