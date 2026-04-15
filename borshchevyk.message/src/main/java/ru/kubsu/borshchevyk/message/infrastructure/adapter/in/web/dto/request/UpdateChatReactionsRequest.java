package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

import java.util.Set;

public record UpdateChatReactionsRequest(Set<String> allowedReactions) {
}