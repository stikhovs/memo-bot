package com.sergio.memo_bot.command_handler.exercise.flash_card;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.ExerciseData;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.FlashCardData;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.VisibleSide;
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

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlashCardsPrepare implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.FLASH_CARDS_PREPARE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        ExerciseData exerciseData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.EXERCISES_RESPONSE, ExerciseData.class);
        List<CardDto> cards = exerciseData.getCards();
        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.FLASH_CARDS_PREPARE)
                .data(new Gson().toJson(cards))
                .build());

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.FLASH_CARD)
                .data(new Gson().toJson(
                        FlashCardData.builder()
                                .totalNumberOfCards(cards.size())
                                .currentIndex(0)
                                .card(cards.getFirst())
                                .visibleSide(VisibleSide.FRONT)
                                .build()))
                .build());

        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .nextCommand(CommandType.FLASH_CARD)
                .previousProcessableMessage(processableMessage)
                .build();
    }
}
