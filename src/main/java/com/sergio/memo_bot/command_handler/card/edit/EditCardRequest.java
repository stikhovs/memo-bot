package com.sergio.memo_bot.command_handler.card.edit;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.reply.Reply;
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

        CardDto card;
        String[] commandAndCardId = processableMessage.getText().split("__");

        if (commandAndCardId[1].equals("%s")) {
            card = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.EDIT_CARD_REQUEST, CardDto.class);
        } else {
            Long cardId = NumberUtils.parseNumber(commandAndCardId[1], Long.class);

            CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);
            card = cardSetDto
                    .getCards()
                    .stream()
                    .filter(cardDto -> cardDto.getId().equals(cardId))
                    .findFirst()
                    .orElseThrow();

            chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                    .command(CommandType.EDIT_CARD_REQUEST)
                    .data(new Gson().toJson(card))
                    .chatId(processableMessage.getChatId())
                    .build());
        }

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
//                .type(BotReplyType.EDIT_MESSAGE_TEXT)
                .text("Какую сторону редактировать?")
//                .messageId(processableMessage.getMessageId())
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of("Лицевая: %s".formatted(card.getFrontSide()), CommandType.EDIT_CARD_FRONT_SIDE_REQUEST),
                        Pair.of("Задняя: %s".formatted(card.getBackSide()), CommandType.EDIT_CARD_BACK_SIDE_REQUEST),
                        Pair.of("Удалить карточку", CommandType.REMOVE_CARD_REQUEST)
                )))
                .build();
    }
}
