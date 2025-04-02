package com.sergio.memo_bot.command_handler.create_set;

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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardSetDto cardSetDto = getCardSetDto(chatId);
        ChatTempData chatTempData = mapToData(chatId, cardSetDto);
        chatTempDataService.clearAndSave(chatId, chatTempData);

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
                .chatId(chatId)
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+2705") + " Добавить еще карточку", CommandType.ADD_CARD_REQUEST),
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " Сохранить набор", CommandType.SAVE_CARD_SET_REQUEST)
                        ))
                )
                .build();
    }

    private ChatTempData mapToData(Long chatId, CardSetDto cardSetDto) {
        return ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.ADD_CARD_RESPONSE)
                .data(new Gson().toJson(cardSetDto))
                .build();
    }

    private CardSetDto getCardSetDto(Long chatId) {
        ChatTempData frontSide = chatTempDataService.get(chatId, CommandType.FRONT_SIDE_RECEIVED);
        ChatTempData backSide = chatTempDataService.get(chatId, CommandType.BACK_SIDE_RECEIVED);

        Optional<ChatTempData> chatTempDataOptional = chatTempDataService.find(chatId, CommandType.ADD_CARD_RESPONSE);

        if (chatTempDataOptional.isPresent()) {
            ChatTempData chatTempData = chatTempDataOptional.get();
            Gson gson = new Gson();
            CardSetDto cardSetDto = gson.fromJson(chatTempData.getData(), CardSetDto.class);
            List<CardDto> cards = cardSetDto.getCards();
            cards.add(CardDto.builder().frontSide(frontSide.getData()).backSide(backSide.getData()).build());
            return cardSetDto.toBuilder()
                    .cards(cards)
                    .build();
        } else {
            ChatTempData title = chatTempDataService.get(chatId, CommandType.NAME_SET);
            return CardSetDto.builder()
                    .uuid(UUID.randomUUID())
                    .telegramChatId(chatId)
                    .title(title.getData())
                    .cards(List.of(CardDto.builder()
                            .frontSide(frontSide.getData())
                            .backSide(backSide.getData())
                            .build()
                    ))
                    .build();
        }
    }

    private String updateTempData(ChatTempData tempData, String frontSide, String backSide) {
        Gson gson = new Gson();
        CardSetDto cardSetDto = gson.fromJson(tempData.getData(), CardSetDto.class);
        CardSetDto updated;
        if (CollectionUtils.isEmpty(cardSetDto.getCards())) {
            updated = cardSetDto.toBuilder()
                    .cards(List.of(CardDto.builder().frontSide(frontSide).backSide(backSide).build()))
                    .build();
        } else {
            List<CardDto> cards = cardSetDto.getCards();
            cards.add(CardDto.builder().frontSide(frontSide).backSide(backSide).build());
            updated = cardSetDto.toBuilder()
                    .cards(cards)
                    .build();
        }
        return gson.toJson(updated);
    }

}
