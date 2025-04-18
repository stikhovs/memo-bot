package com.sergio.memo_bot.persistence.service;

import com.sergio.memo_bot.persistence.entity.ChatMessage;
import com.sergio.memo_bot.persistence.entity.MessageContentType;
import com.sergio.memo_bot.persistence.entity.SenderType;
import com.sergio.memo_bot.persistence.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void saveFromUser(Long chatId, Integer messageId, MessageContentType messageContentType) {
        log.info("Saving to chat_message from user: chatId {}, messageId {}, messageContentType: {}", chatId, messageId, messageContentType);
        chatMessageRepository.findOneByChatIdAndMessageId(chatId, messageId)
                .ifPresentOrElse(it -> {
                            it.setUpdatedAt(LocalDateTime.now());
                            it.setMessageContentType(messageContentType);
                            chatMessageRepository.save(it);
                        },
                        () -> chatMessageRepository.save(ChatMessage.builder()
                                .chatId(chatId)
                                .messageId(messageId)
                                .senderType(SenderType.USER)
                                .messageContentType(messageContentType)
                                .hasButtons(false)
                                .updatedAt(LocalDateTime.now())
                                .build()
                        ));
//        log.info("Saved to chat_message from user: chatId {}, messageId {}", chatId, messageId);
    }

    public void saveFromBot(Long chatId, Integer messageId, boolean hasButtons, MessageContentType messageContentType) {
        log.info("Saving to chat_message from bot: chatId {}, messageId {}, hasButtons {}, messageContentType: {}", chatId, messageId, hasButtons, messageContentType);
        chatMessageRepository.findOneByChatIdAndMessageId(chatId, messageId)
                .ifPresentOrElse(it -> {
//                            it.setUpdatedAt(LocalDateTime.now());
                            it.setHasButtons(hasButtons);
                            it.setMessageContentType(messageContentType);
                            chatMessageRepository.save(it);
                        },
                        () -> chatMessageRepository.save(ChatMessage.builder()
                                .chatId(chatId)
                                .messageId(messageId)
                                .senderType(SenderType.BOT)
                                .messageContentType(messageContentType)
                                .hasButtons(hasButtons)
                                .updatedAt(LocalDateTime.now())
                                .build()
                        ));
//        log.info("Saved to chat_message from bot: chatId {}, messageId {}", chatId, messageId);
    }

    public Optional<Integer> findLastBotMessageId(Long chatId) {
        return chatMessageRepository.findFirstByChatIdAndSenderTypeOrderByUpdatedAtDesc(chatId, SenderType.BOT)
                .map(ChatMessage::getMessageId);
    }

    public Optional<ChatMessage> findLastMessage(Long chatId) {
        return chatMessageRepository.findFirstByChatIdOrderByUpdatedAtDesc(chatId);
    }

    public Integer getMessageId(Long chatId) {
//        log.info("Getting chat messages for chatId {}", chatId);
        return findLastBotMessageId(chatId)
                .orElseThrow(() -> new RuntimeException("Couldn't find any message with chatId %s".formatted(chatId)));
    }

    public List<ChatMessage> findAllBotMessages(Long chatId) {
        return chatMessageRepository.findByChatIdAndSenderTypeOrderByUpdatedAtDesc(chatId, SenderType.BOT);
    }

    public Optional<ChatMessage> findLastWithButtonsMessage(Long chatId) {
        return chatMessageRepository.findFirstByChatIdAndHasButtonsIsTrueOrderByUpdatedAtDesc(chatId);
    }

    public List<ChatMessage> findAllUserMessages(Long chatId) {
        return chatMessageRepository.findByChatIdAndSenderTypeOrderByUpdatedAtDesc(chatId, SenderType.USER);
    }

    public List<ChatMessage> findAllMessages(Long chatId) {
        return chatMessageRepository.findByChatIdOrderByUpdatedAtDesc(chatId);
    }

    public void delete(Long chatId) {
//        log.info("Deleting chat message for chatId {}", chatId);
        chatMessageRepository.deleteByChatId(chatId);
    }

    public void delete(Long chatId, List<Integer> messageIds) {
        log.info("Deleting chat messages for chatId {} and messageIds in {}", chatId, messageIds);
        chatMessageRepository.deleteByChatIdAndMessages(chatId, messageIds);
    }


}
