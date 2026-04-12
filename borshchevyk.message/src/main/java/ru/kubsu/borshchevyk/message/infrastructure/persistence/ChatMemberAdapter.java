package ru.kubsu.borshchevyk.message.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.message.application.port.out.ChatMemberPort;
import ru.kubsu.borshchevyk.message.domain.model.chat.ChatMember;
import ru.kubsu.borshchevyk.message.domain.model.value.ChatId;
import ru.kubsu.borshchevyk.message.domain.model.value.UserId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberEntity;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.entity.ChatMemberId;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.mapper.ChatMemberMapper;
import ru.kubsu.borshchevyk.message.infrastructure.persistence.repository.ChatMemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatMemberAdapter implements ChatMemberPort {

    private final ChatMemberRepository chatMemberRepository;
    private final ChatMemberMapper chatMemberMapper;

    @Override
    public ChatMember save(ChatMember member) {
        ChatMemberEntity entity = chatMemberMapper.toEntity(member);
        ChatMemberEntity saved = chatMemberRepository.save(entity);
        return chatMemberMapper.toDomain(saved);
    }

    @Override
    public void saveAll(List<ChatMember> members) {
        List<ChatMemberEntity> entities = members.stream()
                .map(chatMemberMapper::toEntity)
                .collect(Collectors.toList());
        chatMemberRepository.saveAll(entities);
    }

    @Override
    public Optional<ChatMember> findByChatIdAndUserId(ChatId chatId, UserId userId) {
        ChatMemberId id = new ChatMemberId(chatId.value(), userId.value());
        return chatMemberRepository.findById(id)
                .map(chatMemberMapper::toDomain);
    }

    @Override
    public List<ChatMember> findByChatId(ChatId chatId) {
        return chatMemberRepository.findByChatId(chatId.value())
                .stream()
                .map(chatMemberMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ChatMember> findByChatId(ChatId chatId, Pageable pageable) {
        return chatMemberRepository.findByChatId(chatId.value(), pageable)
                .map(chatMemberMapper::toDomain);
    }

    @Override
    public List<ChatMember> findByUserId(UserId userId) {
        return chatMemberRepository.findByUserId(userId.value())
                .stream()
                .map(chatMemberMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ChatMember member) {
        chatMemberRepository.delete(chatMemberMapper.toEntity(member));
    }
}
