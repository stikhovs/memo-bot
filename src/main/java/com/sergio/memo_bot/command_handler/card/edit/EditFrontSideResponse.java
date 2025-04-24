package com.sergio.memo_bot.command_handler.card.edit;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.external.ApiCallService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.STRING_IS_TOO_LONG;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditFrontSideResponse implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_CARD_FRONT_SIDE_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        if (isBlank(processableMessage.getText()) || length(processableMessage.getText()) > 100) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(STRING_IS_TOO_LONG)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.EDIT_CARD_FRONT_SIDE_REQUEST)
                            .build())
                    .build();
        }

        CardDto cardDto = chatTempDataService.mapDataToType(chatId, CommandType.EDIT_CARD_REQUEST, CardDto.class);
        CardDto updatedCard = apiCallService.updateCard(cardDto.toBuilder().frontSide(processableMessage.getText()).build());
        CardSetDto updatedCardSet = getCardSetAndUpdateIt(chatId, updatedCard);

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .data(new Gson().toJson(updatedCardSet))
                .command(CommandType.GET_CARD_SET_INFO)
                .build());

        return BotMessageReply.builder()
                .chatId(chatId)
//                .type(BotReplyType.MESSAGE)
                .text("Лицевая сторона успешно сохранена")
//                .messageId(processableMessage.getMessageId())
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.GET_CARD_SET_INFO)
                        .previousProcessableMessage(processableMessage.toBuilder().text(CommandType.GET_CARD_SET_INFO.getCommandText()).build())
                        .build())
                .build();
    }

    private CardSetDto getCardSetAndUpdateIt(Long chatId, CardDto updatedCard) {
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class);
        List<CardDto> cards = cardSetDto.getCards().stream().map(it -> {
            if (it.getId().equals(updatedCard.getId())) {
                return updatedCard;
            }
            return it;
        }).toList();
        CardSetDto updatedCardSet = cardSetDto.toBuilder()
                .cards(cards)
                .build();
        return updatedCardSet;
    }
}
