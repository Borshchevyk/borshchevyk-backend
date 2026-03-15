package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.ChangePasswordCommand;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.ChangePasswordRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChangePasswordMapper {
    ChangePasswordCommand toCommand(ChangePasswordRequest request);
}
