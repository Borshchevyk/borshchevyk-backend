package ru.kubsu.borshchevyk.message.domain.model.chat.visitor;

import ru.kubsu.borshchevyk.message.domain.model.chat.type.Channel;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.GroupChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.PrivateChat;
import ru.kubsu.borshchevyk.message.domain.model.chat.type.SavedMessages;

public interface ChatVisitor<T> {
    T visit(PrivateChat chat);
    T visit(GroupChat chat);
    T visit(Channel chat);
    T visit(SavedMessages chat);
}