package com.sergio.memo_bot.command_handler.card_set;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.ApiCallService;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardSetMenuDataRequest implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CARD_SET_MENU_DATA == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        List<CardSetDto> cardSets = apiCallService.getCardSets(chatId);

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.CARD_SET_MENU_DATA)
                .data(new Gson().toJson(cardSets))
                .build());

        return BotPartReply.builder()
                .chatId(chatId)
                .nextCommand(CommandType.CARD_SET_MENU)
                .previousProcessableMessage(processableMessage)
                .build();
    }
}