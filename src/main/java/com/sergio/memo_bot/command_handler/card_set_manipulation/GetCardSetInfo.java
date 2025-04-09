package com.sergio.memo_bot.command_handler.card_set_manipulation;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.List;
import java.util.Optional;

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
        Optional<CardSetDto> cardSet;
        String[] commandAndCardSetId = processableMessage.getText().split("__");

        if (commandAndCardSetId[1].equals("%s")) {
            cardSet = Optional.of(chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class));
        } else {
            Long cardSetId = NumberUtils.parseNumber(commandAndCardSetId[1], Long.class);
            cardSet = apiCallService.getCardSet(cardSetId);
        }

        cardSet.ifPresent(cardSetDto ->
            chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                    .chatId(processableMessage.getChatId())
                    .data(new Gson().toJson(cardSetDto))
                    .command(CommandType.GET_CARD_SET_INFO)
                    .build())
        );

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(cardSet.map(CardSetDto::getTitle).orElse("---"))
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Посмотреть карточки", CommandType.GET_CARDS),
                        Pair.of("Редактировать набор", CommandType.EDIT_SET),
                        Pair.of("Удалить набор", CommandType.REMOVE_SET_REQUEST),
                        cardSet.map(CardSetDto::getCards).filter(CollectionUtils::isNotEmpty).isPresent()
                                ? Pair.of("Упражнения", CommandType.GET_EXERCISES)
                                : Pair.of(null, null),
                        Pair.of("Назад", CommandType.GET_ALL_SETS)
                )))
                .build();

    }

    private static BotReply buildReplyWithButtons(BotReply.BotReplyBuilder MESSAGE, CardSetDto cardSetDto) {
        return MESSAGE
                .text(cardSetDto.getTitle())
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Посмотреть карточки", CommandType.GET_CARDS),
                        Pair.of("Редактировать набор", CommandType.EDIT_SET),
                        Pair.of("Удалить набор", CommandType.REMOVE_SET_REQUEST),
                        Pair.of("Упражнения", CommandType.GET_EXERCISES),
                        Pair.of("Назад", CommandType.GET_ALL_SETS)
                )))
                .build();
    }

}
