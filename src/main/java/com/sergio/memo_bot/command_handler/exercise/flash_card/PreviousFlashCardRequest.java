package com.sergio.memo_bot.command_handler.exercise.flash_card;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.FlashCardData;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.VisibleSide;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreviousFlashCardRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.PREVIOUS_FLASH_CARD_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        FlashCardData data = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.FLASH_CARD, FlashCardData.class);
        List<CardDto> cards = chatTempDataService.mapDataToList(processableMessage.getChatId(), CommandType.FLASH_CARDS_PREPARE, CardDto.class);

        int newIndex = data.getCurrentIndex() - 1;
        CardDto previousCard = cards.get(newIndex);

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.FLASH_CARD)
                .data(new Gson().toJson(data.toBuilder()
                        .card(previousCard)
                        .currentIndex(newIndex)
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
