package ru.kubsu.borshchevyk.message.domain.model.call;

import java.util.Set;
import java.util.stream.Collectors;

public enum CallEventType {
    INITIATED {
        @Override
        public Set<String> determineTargetParticipants(Set<String> allParticipants, String actorId, String initiatorId) {
            return allParticipants.stream()
                    .filter(id -> !id.equals(actorId))
                    .collect(Collectors.toSet());
        }
    },
    ACCEPTED {
        @Override
        public Set<String> determineTargetParticipants(Set<String> allParticipants, String actorId, String initiatorId) {
            return allParticipants.stream()
                    .filter(id -> id.equals(initiatorId) && !id.equals(actorId))
                    .collect(Collectors.toSet());
        }
    },
    REJECTED {
        @Override
        public Set<String> determineTargetParticipants(Set<String> allParticipants, String actorId, String initiatorId) {
            return allParticipants.stream()
                    .filter(id -> id.equals(initiatorId) && !id.equals(actorId))
                    .collect(Collectors.toSet());
        }
    },
    ENDED {
        @Override
        public Set<String> determineTargetParticipants(Set<String> allParticipants, String actorId, String initiatorId) {
            return allParticipants.stream()
                    .filter(id -> !id.equals(actorId))
                    .collect(Collectors.toSet());
        }
    },
    DEFAULT {
        @Override
        public Set<String> determineTargetParticipants(Set<String> allParticipants, String actorId, String initiatorId) {
            return allParticipants;
        }
    };

    public abstract Set<String> determineTargetParticipants(Set<String> allParticipants, String actorId, String initiatorId);

    public static CallEventType fromString(String type) {
        if (type == null) return DEFAULT;
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }
}