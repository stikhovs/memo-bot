package com.sergio.memo_bot.command_handler.card_set_manipulation.remove_card;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveCardResponse implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.REMOVE_CARD_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardDto cardDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.EDIT_CARD_REQUEST, CardDto.class);
        apiCallService.deleteCard(cardDto.getId());
        chatTempDataService.clear(chatId, CommandType.EDIT_CARD_REQUEST);

        CardSetDto updatedCardSet = removeCardFromDB(chatId, cardDto.getId());
        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .data(new Gson().toJson(updatedCardSet))
                .command(CommandType.GET_CARD_SET_INFO)
                .build());

        return MultipleBotReply.builder()
                .type(BotReplyType.MESSAGE)
                .chatId(chatId)
                .messageId(processableMessage.getMessageId())
                .text("Карточка удалена")
//                .previousProcessableMessage(processableMessage.toBuilder().text(CommandType.GET_CARD_SET_INFO.getCommandText()).build())
                .nextCommand(CommandType.GET_CARD_SET_INFO)
                .build();
    }

    private CardSetDto removeCardFromDB(Long chatId, Long cardId) {
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class);
        List<CardDto> cards = cardSetDto.getCards().stream()
                .filter(it -> !ObjectUtils.notEqual(it.getId(), cardId))
                .toList();
        CardSetDto updatedCardSet = cardSetDto.toBuilder()
                .cards(cards)
                .build();
        return updatedCardSet;
    }
}
