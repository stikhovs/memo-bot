package com.sergio.memo_bot.command_handler.exercise.flash_card;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
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
public class FlashCards implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.FLASH_CARDS == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);
        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.FLASH_CARDS)
                .data(new Gson().toJson(cardSetDto.getCards()))
                .build());

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.FLASH_CARD)
                .data(new Gson().toJson(
                        FlashCardData.builder()
                                .totalNumberOfCards(cardSetDto.getCards().size())
                                .currentIndex(0)
                                .card(cardSetDto.getCards().getFirst())
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
