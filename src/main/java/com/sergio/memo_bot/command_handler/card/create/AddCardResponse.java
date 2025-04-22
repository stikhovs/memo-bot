package com.sergio.memo_bot.command_handler.card.create;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

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

        return BotMessageReply.builder()
                .text(
                        CARD_CREATION_RESPONSE.formatted(cardSetDto.getTitle(),
                                cardSetDto.getCards()
                                        .stream()
                                        .map(cardDto -> cardDto.getFrontSide() + " -> " + cardDto.getBackSide())
                                        .collect(Collectors.joining(";\n"))
                        )
                )
                .chatId(chatId)
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(ADD_ONE_MORE_CARD, CommandType.ADD_CARD_REQUEST),
                                Pair.of(SAVE_CARD_SET, CommandType.SAVE_CARD_SET_REQUEST)
                        ))
                )
                .parseMode(ParseMode.HTML)
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
            Optional<ChatTempData> titleOptional = chatTempDataService.find(chatId, CommandType.NAME_SET_RESPONSE);
            if (titleOptional.isPresent()) {
                ChatTempData title = titleOptional.get();

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
            } else {
                CardSetDto cardSetDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class);
                CardDto cardDto = CardDto.builder()
                        .frontSide(frontSide.getData())
                        .backSide(backSide.getData())
                        .build();

                List<CardDto> cards = cardSetDto.getCards();
                cards.add(cardDto);

                return cardSetDto.toBuilder()
                        .cards(cards)
                        .build();
            }
        }
    }

}
