package com.sergio.memo_bot.command_handler.exercise;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Exercises implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_EXERCISES == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Выберите упражнение")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Флеш-карточки", CommandType.FLASH_CARDS_PREPARE),
                        Pair.of("Квиз", CommandType.QUIZ_PREPARE),
                        Pair.of("Самостоятельный ввод ответа", CommandType.ANSWER_INPUT_PREPARE)
                )))
                .build();
    }
}
