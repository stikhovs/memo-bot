package com.sergio.memo_bot.scheduling;

import com.sergio.memo_bot.persistence.entity.ChatMessage;
import com.sergio.memo_bot.persistence.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCleanupScheduler {
    private static final int MAX_HOURS_TTL = 48;
    private static final int MAX_NUMBER_OF_MESSAGES = 100;

    private final TelegramClient memoBotClient;
    private final ChatMessageService chatMessageService;

    @Transactional
    @Scheduled(fixedDelayString = "${bot.cleanup.scheduler.delay-ms:3600000}")
    public void cleanupOldMessages() {
        log.info("Checking for old messages to be cleaned up...");

        List<ChatMessage> oldMessages = chatMessageService.findByCreatedAtBefore(LocalDateTime.now().minusHours(MAX_HOURS_TTL));
        Map<Long, List<ChatMessage>> messagesByChatId = oldMessages.stream()
                .collect(Collectors.groupingBy(ChatMessage::getChatId));

        messagesByChatId.forEach((chatId, messages) -> {
            List<Integer> messageIds = messages.stream().map(ChatMessage::getMessageId).toList();

            splitIfNecessary(messageIds).forEach(splitIds -> {
                DeleteMessages deleteMessages = DeleteMessages.builder()
                        .chatId(chatId)
                        .messageIds(splitIds)
                        .build();
                delete(deleteMessages);
            });
            chatMessageService.delete(chatId, messageIds);
            log.info("Scheduled cleanup is done for chat {}. Following messages were deleted: {} ", chatId, messageIds);
        });
    }

    private void delete(DeleteMessages deleteMessages) {
        try {
            if (isNotEmpty(deleteMessages.getMessageIds())) {
                memoBotClient.execute(deleteMessages);
            }
        } catch (TelegramApiException e) {
            log.error("Failed to delete messages in telegram. It will be deleted in the database. {}", deleteMessages, e);
        }
    }

    private List<List<Integer>> splitIfNecessary(List<Integer> ids) {
        List<List<Integer>> result = new ArrayList<>();
        if (size(ids) > MAX_NUMBER_OF_MESSAGES) {
            List<Integer> limited = ids.stream().limit(MAX_NUMBER_OF_MESSAGES).toList();
            result.add(limited);

            List<List<Integer>> split = splitIfNecessary(ids.stream().skip(MAX_NUMBER_OF_MESSAGES).toList());
            result.addAll(split);
        } else {
            result.add(ids);
        }
        return result;
    }

}
