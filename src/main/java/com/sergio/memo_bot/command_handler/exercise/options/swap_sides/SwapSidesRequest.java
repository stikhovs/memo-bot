package com.sergio.memo_bot.command_handler.exercise.options.swap_sides;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.ExerciseData;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.SWAPPED;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.SWAPPED_BACK;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwapSidesRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SWAP_SIDES_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        ExerciseData exerciseData = chatTempDataService.mapDataToType(chatId, CommandType.EXERCISES_RESPONSE, ExerciseData.class);

        exerciseData.setSwappedFrontBack(!exerciseData.isSwappedFrontBack());

        if (exerciseData.isUsePagination()) {
            swapSidesInPages(exerciseData);
            swapCards(exerciseData.getCards());
        } else {
            swapCards(exerciseData.getCards());
        }

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.EXERCISES_RESPONSE)
                        .data(new Gson().toJson(exerciseData))
                .build());

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(exerciseData.isSwappedFrontBack() ? SWAPPED : SWAPPED_BACK)
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.EXERCISES_RESPONSE)
                        .build())
                .build();
    }

    private void swapCards(List<CardDto> exerciseData) {
        exerciseData.forEach(this::swapSides);
    }

    private void swapSidesInPages(ExerciseData exerciseData) {
        exerciseData.getCardPages().forEach(this::swapCards);
    }


    private void swapSides(CardDto cardDto) {
        String frontSide = cardDto.getFrontSide();
        String backSide = cardDto.getBackSide();

        cardDto.setFrontSide(backSide);
        cardDto.setBackSide(frontSide);
    }
}