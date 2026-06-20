package ru.kubsu.borshchevyk.message.infrastructure.adapter.in.web.dto.request;
import ru.kubsu.borshchevyk.message.domain.exception.*;

import java.util.Set;


public record UpdateChatReactionsRequest(Set<String> allowedReactions) {
}
