package com.sergio.memo_bot.command_handler.card_set_manipulation;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditCardRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_CARD_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long cardId = NumberUtils.parseNumber(processableMessage.getText().split("__")[1], Long.class);

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);
        CardDto card = cardSetDto.getCards().stream().filter(cardDto -> cardDto.getId().equals(cardId)).findFirst().orElseThrow();

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                        .command(CommandType.EDIT_CARD_REQUEST)
                        .data(new Gson().toJson(card))
                        .chatId(processableMessage.getChatId())
                .build());

        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Какую сторону редактировать?")
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of("Лицевая: %s".formatted(card.getFrontSide()), CommandType.EDIT_CARD_FRONT_SIDE_REQUEST),
                        Pair.of("Задняя: %s".formatted(card.getBackSide()), CommandType.EDIT_CARD_BACK_SIDE_REQUEST)
                )))
                .build();
    }
}
