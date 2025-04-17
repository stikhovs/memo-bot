package com.sergio.memo_bot.command_handler.exercise.flash_card;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.ExerciseData;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.FlashCardData;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.VisibleSide;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.FLASH_CARDS_PREPARE)
                .data(new Gson().toJson(exerciseData.getCards()))
                .build());

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.FLASH_CARD)
                .data(new Gson().toJson(
                        FlashCardData.builder()
                                .totalNumberOfCards(exerciseData.getCards().size())
                                .currentIndex(0)
                                .card(exerciseData.getCards().getFirst())
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
