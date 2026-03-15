package ru.kubsu.borshchevyk.auth.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.kubsu.borshchevyk.auth.application.dto.command.VerifyCommand;
import ru.kubsu.borshchevyk.auth.domain.model.result.VerifyResult;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.request.VerifyRequest;
import ru.kubsu.borshchevyk.auth.infrastructure.adapter.in.dto.response.VerifyResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VerifyMapper {
    VerifyCommand toCommand(VerifyRequest request);
    VerifyResponse toResponse(VerifyResult result);
}
