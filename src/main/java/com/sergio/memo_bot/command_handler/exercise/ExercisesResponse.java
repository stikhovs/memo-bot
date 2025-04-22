package com.sergio.memo_bot.command_handler.exercise;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExercisesResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXERCISES_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CommandType sourceCommand = defineSource(chatId);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOOSE_EXERCISE)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(FLASH_CARDS, CommandType.FLASH_CARDS_PREPARE),
                        Pair.of(QUIZ, CommandType.QUIZ_PREPARE),
                        Pair.of(INPUT_ANSWER, CommandType.ANSWER_INPUT_PREPARE),
                        Pair.of(FIND_PAIRS, CommandType.CONNECT_WORDS_PREPARE),
                        Pair.of(BACK, defineBackButton(sourceCommand))
                )))
                .build();
    }

    private CommandType defineBackButton(CommandType sourceCommand) {
        return switch (sourceCommand) {
            case EXERCISES_FROM_CARD_SET -> CommandType.GET_CARD_SET_INFO;
            case EXERCISES_FROM_CATEGORY -> CommandType.GET_CATEGORY_INFO_RESPONSE;
            default -> CommandType.EXERCISES_FROM_MAIN_MENU;
        };
    }

    private CommandType defineSource(Long chatId) {
        return chatTempDataService.find(chatId, CommandType.EXERCISES_DATA_PREPARE)
                .map(chatTempData -> CommandType.getByCommandText(chatTempData.getData()))
                .orElse(CommandType.EXERCISES_FROM_MAIN_MENU);
    }
}
