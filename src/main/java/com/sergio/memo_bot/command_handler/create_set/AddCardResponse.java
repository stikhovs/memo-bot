package com.sergio.memo_bot.command_handler.create_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddCardResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.ADD_CARD_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CardSetDto.class);

        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text(
                        """
                                Предварительный набор \"%s\"
                                Карточки: \n%s
                                """.formatted(cardSetDto.getTitle(),
                                cardSetDto.getCards()
                                        .stream()
                                        .map(cardDto -> cardDto.getFrontSide() + " -> " + cardDto.getBackSide())
                                        .collect(Collectors.joining(";\n"))
                        )
                )
                .messageId(processableMessage.getMessageId())
                .chatId(processableMessage.getChatId())
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+2705") + " Добавить еще карточку", CommandType.ADD_CARD_REQUEST),
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " Сохранить набор", CommandType.SAVE_CARD_SET_REQUEST)
                        ))
                )
                .build();
    }
}
