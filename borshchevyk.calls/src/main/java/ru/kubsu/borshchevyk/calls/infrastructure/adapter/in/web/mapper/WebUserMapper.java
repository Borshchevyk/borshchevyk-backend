package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.calls.domain.model.User;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.UserResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebUserMapper {
    UserResponse toDto(User user);
}
