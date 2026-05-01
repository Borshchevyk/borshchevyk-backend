package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;

import java.util.Set;

/**
 * @author Aleksey Timko
 */
public record UpdateChatReactionsRequest(Set<String> allowedReactions) {
}