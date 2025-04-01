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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.List;

import static com.sergio.memo_bot.util.UrlConstant.GET_SET_AND_CARDS_URL;

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
        ResponseEntity<CardSetDto> setAndCardsResponse = getSetAndCards(cardSetId);

        if (setAndCardsResponse.getStatusCode().is2xxSuccessful()) {
            CardSetDto cardSetDto = setAndCardsResponse.getBody();

            chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                    .chatId(processableMessage.getChatId())
                    .data(new Gson().toJson(cardSetDto))
                    .command(CommandType.GET_CARD_SET_INFO)
                    .build());

            return BotReply.builder()
                    .type(BotReplyType.EDIT_MESSAGE_TEXT)
                    .chatId(processableMessage.getChatId())
                    .messageId(processableMessage.getMessageId())
                    .text(cardSetDto.getTitle())
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                            Pair.of("Посмотреть карточки", CommandType.GET_CARDS),
                            Pair.of("Редактировать набор", CommandType.EDIT_SET),
                            Pair.of("Упражнения", CommandType.GET_EXERCISES),
                            Pair.of("Назад", CommandType.GET_ALL_SETS)
                    )))
                    .build();
        }

        return MultipleBotReply.builder()
                .type(BotReplyType.MESSAGE)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .text("Что-то пошло не так. Попробуйте еще раз")
                .nextCommand(CommandType.MAIN_MENU)
                .previousProcessableMessage(processableMessage)
                .messageId(processableMessage.getMessageId())
                .build();
    }

    private ResponseEntity<CardSetDto> getSetAndCards(Long cardSetId) {
        return apiCallService.get(GET_SET_AND_CARDS_URL.formatted(cardSetId), CardSetDto.class);
    }
}
