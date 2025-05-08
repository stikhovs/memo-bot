package com.sergio.memo_bot.command_handler.exercise;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExerciseDataSizeCheck implements CommandHandler {

    public static final int MAX_CARDS_NUMBER_FOR_ONE_PAGE = 15;

    private final ChatTempDataService chatTempDataService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXERCISES_DATA_SIZE_CHECK == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        ExerciseData exerciseData = chatTempDataService.mapDataToType(chatId, CommandType.EXERCISES_RESPONSE, ExerciseData.class);

        if (exerciseData.getCards().size() > MAX_CARDS_NUMBER_FOR_ONE_PAGE) {
            exerciseData.setUsePagination(true);
            exerciseData.setCardPages(splitToPages(exerciseData.getCards()));

            chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                    .chatId(chatId)
                    .command(CommandType.EXERCISES_RESPONSE)
                    .data(new Gson().toJson(exerciseData))
                    .build());
        }

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.EXERCISES_RESPONSE)
                .build();
    }

    private List<List<CardDto>> splitToPages(List<CardDto> cards) {
        List<List<CardDto>> result = new ArrayList<>();

        List<CardDto> limited = cards.stream().limit(MAX_CARDS_NUMBER_FOR_ONE_PAGE).toList();
        result.add(limited);

        List<CardDto> rest = cards.stream().skip(MAX_CARDS_NUMBER_FOR_ONE_PAGE).toList();
        if (rest.size() > MAX_CARDS_NUMBER_FOR_ONE_PAGE) {
            result.addAll(splitToPages(rest));
        } else {
            result.add(rest);
        }

        return result;
    }

}