package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc.UserGrpcClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


import ru.kubsu.borshchevyk.grpc.UserResponse;

@Service
@RequiredArgsConstructor
public class UserEnrichmentService {

    private final UserGrpcClient userGrpcClient;

    public ShortUserDto enrichUser(UUID userId) {
        try {
            ru.kubsu.borshchevyk.grpc.UserResponse response = userGrpcClient.getUserInfo(userId);
            return mapToEnriched(response);
        } catch (Exception e) {
            return ShortUserDto.builder()
                    .id(userId)
                    .firstName("User")
                    .lastName(userId.toString().substring(0, 8))
                    .tag("unknown")
                    .build();
        }
    }

    public List<ShortUserDto> enrichUsers(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        try {
            List<ru.kubsu.borshchevyk.grpc.UserResponse> responses = userGrpcClient.getUsersBatch(userIds);
            return responses.stream()
                    .map(this::mapToEnriched)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return userIds.stream()
                    .map(id -> ShortUserDto.builder()
                            .id(id)
                            .firstName("User")
                            .lastName(id.toString().substring(0, 8))
                            .build())
                    .collect(Collectors.toList());
        }
    }

    public Map<UUID, ShortUserDto> enrichUsersToMap(List<UUID> userIds) {
        return enrichUsers(userIds).stream()
                .collect(Collectors.toMap(ShortUserDto::id, u -> u));
    }

    private ShortUserDto mapToEnriched(ru.kubsu.borshchevyk.grpc.UserResponse response) {
        return ShortUserDto.builder()
                .id(UUID.fromString(response.getUserId()))
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .tag(response.getTag())
                .avatarUrl(response.getAvatarUrl().isEmpty() ? null : response.getAvatarUrl())
                .build();
    }
}
