package ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.in.web.dto.response.ShortUserDto;
import ru.kubsu.borshchevyk.calls.infrastructure.adapter.out.grpc.UserGrpcClient;
import ru.kubsu.borshchevyk.grpc.UserResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service to enrich call data with short user info via gRPC.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEnrichmentService {

    private final UserGrpcClient userGrpcClient;

    public ShortUserDto getUserInfo(UUID userId) {
        try {
            UserResponse response = userGrpcClient.getUserInfo(userId);
            return mapToEnriched(response);
        } catch (Exception e) {
            log.error("Failed to fetch short user info for user {}: {}", userId, e.getMessage());
            return createFallbackUserDto(userId);
        }
    }

    public Map<UUID, ShortUserDto> getUsersBatch(List<UUID> userIds) {
        try {
            if (userIds.isEmpty()) {
                return Map.of();
            }
            List<UserResponse> responses = userGrpcClient.getUsersBatch(userIds);
            return responses.stream()
                    .map(this::mapToEnriched)
                    .collect(Collectors.toMap(ShortUserDto::id, dto -> dto));
        } catch (Exception e) {
            log.error("Failed to fetch short users info batch for users {}: {}", userIds, e.getMessage());
            return userIds.stream().collect(Collectors.toMap(
                    id -> id,
                    this::createFallbackUserDto
            ));
        }
    }

    private ShortUserDto mapToEnriched(UserResponse response) {
        return ShortUserDto.builder()
                .id(UUID.fromString(response.getUserId()))
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .tag(response.getTag())
                .avatarUrl(response.getAvatarUrl().isEmpty() ? null : response.getAvatarUrl())
                .build();
    }

    private ShortUserDto createFallbackUserDto(UUID userId) {
        return ShortUserDto.builder()
                .id(userId)
                .firstName("Unknown")
                .lastName("User")
                .tag("unknown")
                .build();
    }
}
