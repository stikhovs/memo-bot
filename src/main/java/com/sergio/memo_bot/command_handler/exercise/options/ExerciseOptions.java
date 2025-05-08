package com.sergio.memo_bot.command_handler.exercise.options;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExerciseOptions implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXERCISE_DATA_OPTIONS == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOOSE_EXERCISE_DATA_OPTIONS)
                .parseMode(ParseMode.HTML)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(SHUFFLE_CARDS, CommandType.SHUFFLE_CARDS_REQUEST),
                        Pair.of(SWAP_FRONT_AND_BACK_SIDES, CommandType.SWAP_SIDES_REQUEST),
                        Pair.of(BACK, CommandType.EXERCISES_RESPONSE)
                )))
                .build();
    }
}