package com.sergio.memo_bot.command_handler.exercise.options.shuffle_cards;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.ExerciseData;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sergio.memo_bot.command_handler.exercise.ExerciseDataSizeCheck.MAX_CARDS_NUMBER_FOR_ONE_PAGE;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CARDS_ARE_SHUFFLED;


@Slf4j
@Component
@RequiredArgsConstructor
public class ShuffleCardsRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SHUFFLE_CARDS_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        ExerciseData exerciseData = chatTempDataService.mapDataToType(chatId, CommandType.EXERCISES_RESPONSE, ExerciseData.class);

        if (exerciseData.isUsePagination()) {
            shuffleCards(exerciseData.getCards());
            shuffleCardPages(exerciseData);
        } else {
            shuffleCards(exerciseData.getCards());
        }

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.EXERCISES_RESPONSE)
                .data(new Gson().toJson(exerciseData))
                .build());

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CARDS_ARE_SHUFFLED)
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.EXERCISES_RESPONSE)
                        .build())
                .build();
    }

    private void shuffleCards(List<CardDto> cards) {
        Collections.shuffle(cards);
    }

    private void shuffleCardPages(ExerciseData exerciseData) {
        List<List<CardDto>> shuffledPages = splitToPages(exerciseData.getCards());
        exerciseData.setCardPages(shuffledPages);
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