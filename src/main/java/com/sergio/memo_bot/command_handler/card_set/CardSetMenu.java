package com.sergio.memo_bot.command_handler.card_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardSetMenu implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CARD_SET_MENU == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Наборы")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Создать набор", CommandType.SET_CREATION_START),
                        Pair.of("Выбрать набор", CommandType.CHOOSE_SET_REQUEST),
                        Pair.of("Импортировать набор", CommandType.IMPORT_CARD_SET_MENU),
                        Pair.of("Назад", CommandType.MAIN_MENU)
                )))
                .build();
    }
}