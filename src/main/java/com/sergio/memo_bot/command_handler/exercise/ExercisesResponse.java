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
import static org.apache.commons.collections4.CollectionUtils.size;

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

        ExerciseData exerciseData = chatTempDataService.mapDataToType(chatId, CommandType.EXERCISES_RESPONSE, ExerciseData.class);
        chatTempDataService.clear(chatId, CommandType.ANSWER_INPUT_REQUEST);
        chatTempDataService.clear(chatId, CommandType.CONNECT_WORDS_REQUEST);
        chatTempDataService.clear(chatId, CommandType.QUIZ);

        CommandType sourceCommand = defineSource(chatId);

        if (exerciseData.isUsePagination()) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(PAGEABLE_EXERCISE_EXPLANATION.formatted(size(exerciseData.getCardPages())))
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(getButtons(sourceCommand)))
                    .build();

        } else {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(CHOOSE_EXERCISE)
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(getButtons(sourceCommand)))
                    .build();
        }
    }

    private List<Pair<String, CommandType>> getButtons(CommandType sourceCommand) {
        return List.of(
                Pair.of(FLASH_CARDS, CommandType.FLASH_CARDS_PREPARE),
                Pair.of(QUIZ, CommandType.QUIZ_PREPARE),
                Pair.of(INPUT_ANSWER, CommandType.ANSWER_INPUT_PREPARE),
                Pair.of(FIND_PAIRS, CommandType.CONNECT_WORDS_PREPARE),
                Pair.of(EXERCISE_DATA_OPTIONS, CommandType.EXERCISE_DATA_OPTIONS),
                Pair.of(BACK, defineBackButton(sourceCommand))
        );
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
