package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.response.EnrichedUserResponse;
import ru.kubsu.borshchevyk.message.infrastructure.adapter.out.grpc.UserGrpcClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserEnrichmentService {

    private final UserGrpcClient userGrpcClient;

    public EnrichedUserResponse enrichUser(UUID userId) {
        try {
            ru.kubsu.borshchevyk.grpc.UserResponse response = userGrpcClient.getUserInfo(userId);
            return mapToEnriched(response);
        } catch (Exception e) {
            return EnrichedUserResponse.builder()
                    .id(userId)
                    .firstName("User")
                    .lastName(userId.toString().substring(0, 8))
                    .tag("unknown")
                    .build();
        }
    }

    public List<EnrichedUserResponse> enrichUsers(List<UUID> userIds) {
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
                    .map(id -> EnrichedUserResponse.builder()
                            .id(id)
                            .firstName("User")
                            .lastName(id.toString().substring(0, 8))
                            .build())
                    .collect(Collectors.toList());
        }
    }

    public Map<UUID, EnrichedUserResponse> enrichUsersToMap(List<UUID> userIds) {
        return enrichUsers(userIds).stream()
                .collect(Collectors.toMap(EnrichedUserResponse::id, u -> u));
    }

    private EnrichedUserResponse mapToEnriched(ru.kubsu.borshchevyk.grpc.UserResponse response) {
        return EnrichedUserResponse.builder()
                .id(UUID.fromString(response.getUserId()))
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .tag(response.getTag())
                .avatarUrl(response.getAvatarUrl().isEmpty() ? null : response.getAvatarUrl())
                .build();
    }
}
