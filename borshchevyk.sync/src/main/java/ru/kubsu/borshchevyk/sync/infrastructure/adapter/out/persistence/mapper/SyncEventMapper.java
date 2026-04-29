package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;
import ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.entity.SyncEventEntity;

import java.util.List;

/**
 * Mapper for converting between SyncEvent domain model and SyncEventEntity.
 *
 * @author Aleksey Timko
 */
@Mapper(componentModel = "spring")
public interface SyncEventMapper {

    @Mapping(target = "eventId", source = "id")
    SyncEvent toDomain(SyncEventEntity entity);

    @Mapping(target = "id", source = "eventId")
    SyncEventEntity toEntity(SyncEvent domain);

    List<SyncEvent> toDomainList(List<SyncEventEntity> entities);
}