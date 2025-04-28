package com.sergio.memo_bot.command_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatMessageService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.DeleteMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainMenu implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;
    private final ChatTempDataService chatTempDataService;
    private final ChatMessageService chatMessageService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.MAIN_MENU == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);
        chatTempDataService.clear(chatId);

        List<Integer> messagesToDelete = chatMessageService.findAllMessages(chatId).stream()
                .filter(chatMessage -> chatMessage.getCreatedAt().isAfter(LocalDateTime.now().minusHours(48)))
                .map(ChatMessage::getMessageId)
                .filter(Objects::nonNull)
                .limit(101)
                .skip(1)
                .toList();

        if (messagesToDelete.isEmpty()) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(MAIN_MENU)
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                            Pair.of(CARD_SETS, CommandType.CARD_SET_MENU_DATA),
                            Pair.of(CATEGORIES, CommandType.CATEGORY_MENU_DATA),
                            Pair.of(EXERCISES, CommandType.EXERCISES_FROM_MAIN_MENU)
                    )))
                    .parseMode(ParseMode.HTML)
                    .build();
        }

        return DeleteMessageReply.builder()
                .chatId(chatId)
                .messageIds(messagesToDelete)
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.MAIN_MENU)
                        .build())
                .build();
    }

}
