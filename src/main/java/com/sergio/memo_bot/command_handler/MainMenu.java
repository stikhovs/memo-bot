package com.sergio.memo_bot.command_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainMenu implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;
    private final ChatTempDataService chatTempDataService;
//    private final ChatMessageService chatMessageService;

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

        return BotMessageReply.builder()
                .chatId(chatId)
                .text("Выберите действие")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Категории", CommandType.CATEGORY_MENU_DATA),
                        Pair.of("Наборы", CommandType.CARD_SET_MENU_DATA),
                        Pair.of("Упражнения", CommandType.EXERCISES_FROM_MAIN_MENU)
                )))
                /*.replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Создать категорию", CommandType.CREATE_CATEGORY_REQUEST),
                        Pair.of("Создать набор", CommandType.CREATE_SET),
                        Pair.of("Просмотреть наборы", CommandType.GET_ALL_SETS)
                )))*/
                .build();


        /*List<ChatMessage> allMessages = chatMessageService.findAllMessages(processableMessage.getChatId());
        ChatMessage chatMessage = allMessages.getFirst();

        if (chatMessage.getSenderType() == SenderType.USER) {
            Optional<ChatMessage> lastWithButtonsMessage = chatMessageService.findLastWithButtonsMessage(processableMessage.getChatId());
            if (lastWithButtonsMessage.isPresent()) {
                return BotReply.builder()
                        .type(BotReplyType.EDIT_MESSAGE_REPLY_MARKUP)
                        .chatId(processableMessage.getChatId())
                        .messageId(lastWithButtonsMessage.get().getMessageId())
                        .nextReply(
                                BotReply.builder()
                                        .type(BotReplyType.MESSAGE)
                                        .text("Выберите действие")
                                        .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                                                Pair.of("Создать набор", CommandType.CREATE_SET),
                                                Pair.of("Просмотреть наборы", CommandType.GET_ALL_SETS)
                                        )))
                                        .chatId(chatId)
                                        .build()
                        )
                        .build();
            } else {
                return BotReply.builder()
                        .type(BotReplyType.MESSAGE)
                        .text("Выберите действие")
                        .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                                Pair.of("Создать набор", CommandType.CREATE_SET),
                                Pair.of("Просмотреть наборы", CommandType.GET_ALL_SETS)
                        )))
                        .chatId(chatId)
                        .build();
            }
        } else if (chatMessage.isHasButtons()) {
            return BotReply.builder()
                    .type(BotReplyType.EDIT_MESSAGE_REPLY_MARKUP)
                    .chatId(processableMessage.getChatId())
                    .messageId(chatMessage.getMessageId())
                    .nextReply(
                            BotReply.builder()
                                    .type(BotReplyType.MESSAGE)
                                    .text("Выберите действие")
                                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                                            Pair.of("Создать набор", CommandType.CREATE_SET),
                                            Pair.of("Просмотреть наборы", CommandType.GET_ALL_SETS)
                                    )))
                                    .chatId(chatId)
                                    .build()
                    )
                    .build();
        } else {
            return BotReply.builder()
                    .type(BotReplyType.EDIT_MESSAGE_TEXT)
                    .messageId(chatMessage.getMessageId())
                    .text("Выберите действие")
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                            Pair.of("Создать набор", CommandType.CREATE_SET),
                            Pair.of("Просмотреть наборы", CommandType.GET_ALL_SETS)
                    )))
                    .chatId(chatId)
                    .build();
        }*/
    }

}
