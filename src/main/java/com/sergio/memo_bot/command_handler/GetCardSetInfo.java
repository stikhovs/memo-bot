package com.sergio.memo_bot.command_handler;

import com.google.gson.Gson;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.List;

import static com.sergio.memo_bot.util.UrlConstant.GET_CARDS_URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCardSetInfo implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ApiCallService apiCallService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_CARD_SET_INFO == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long cardSetId = NumberUtils.parseNumber(processableMessage.getText().split("__")[1], Long.class);

        ResponseEntity<List<CardDto>> cards = getCards(cardSetId);

        List<CardSetDto> cardSets = chatTempDataService.mapDataToList(processableMessage.getChatId(), CardSetDto.class);

        CardSetDto resultCardSet = cardSets.stream()
                .filter(cardSetDto -> cardSetDto.getId().equals(cardSetId))
                .map(cardSetDto -> cardSetDto.toBuilder().cards(cards.getBody()).build())
                .findFirst()
                .orElseThrow();

        chatTempDataService.clearAndSave(processableMessage.getChatId(),
                ChatTempData.builder()
                        .chatId(processableMessage.getChatId())
                        .data(new Gson().toJson(resultCardSet))
                        .build());


        return BotReply.builder()
                .type(BotReplyType.EDIT_MESSAGE_TEXT)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .text(resultCardSet.getTitle())
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Посмотреть карточки", CommandType.MAIN_MENU),
                        Pair.of("Редактировать карточки", CommandType.MAIN_MENU),
                        Pair.of("Упражнения", CommandType.MAIN_MENU)
                )))
                .build();
    }

    private ResponseEntity<List<CardDto>> getCards(Long cardSetId) {
        return apiCallService.get(GET_CARDS_URL.formatted(cardSetId));
    }
}
