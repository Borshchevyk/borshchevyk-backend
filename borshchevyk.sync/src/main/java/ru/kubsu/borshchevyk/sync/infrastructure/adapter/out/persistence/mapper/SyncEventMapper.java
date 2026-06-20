package ru.kubsu.borshchevyk.sync.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
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

    SyncEvent toDomain(SyncEventEntity entity);

    SyncEventEntity toEntity(SyncEvent domain);

    List<SyncEvent> toDomainList(List<SyncEventEntity> entities);
}