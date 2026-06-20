package ru.kubsu.borshchevyk.message.infrastructure.event;

public final class WebSocketTopics {

    private WebSocketTopics() {
        // Prevent instantiation
    }

    public static final String CHAT_MEMBERS_TOPIC_TEMPLATE = "/topic/chat/%s/members";
    
    public static String getChatMembersTopic(String chatId) {
        return String.format(CHAT_MEMBERS_TOPIC_TEMPLATE, chatId);
    }
}