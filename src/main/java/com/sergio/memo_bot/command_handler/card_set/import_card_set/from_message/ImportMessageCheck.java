package com.sergio.memo_bot.command_handler.card_set.import_card_set.from_message;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.EmojiConverter;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.length;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportMessageCheck implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_MESSAGE_CHECK == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);

        CardSetDto parsedCardSet = parse(processableMessage.getText());

        if (parsedCardSet == null) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(COULD_NOT_PARSE_MESSAGE)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.IMPORT_CARD_SET_FROM_MESSAGE_MENU)
                            .build())
                    .build();
        }

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.IMPORT_MESSAGE_RESPONSE)
                        .data(new Gson().toJson(parsedCardSet))
                .build());

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(IMPORT_CARD_SET_FROM_MESSAGE_CHECK.formatted(
                        parsedCardSet.getTitle(),
                        parsedCardSet.getCards().stream()
                                .map(cardDto -> cardDto.getFrontSide() + " – " + cardDto.getBackSide())
                                .collect(Collectors.joining("\n"))
                        )
                )
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of(CANCEL_IMPORT, CommandType.IMPORT_CARD_SET_FROM_MESSAGE_MENU),
                        Pair.of(CONTINUE_IMPORT, CommandType.IMPORT_MESSAGE_RESPONSE)
                )))
                .build();
    }

    private CardSetDto parse(String message) {
        String n = "\n";
        String openQuote = EmojiConverter.getEmoji("U+201E");
        String closeQuote = EmojiConverter.getEmoji("U+201C");
        String thinsp = EmojiConverter.getEmoji("U+2009");
        String mediumDash = EmojiConverter.getEmoji("U+2013");

        String[] titleAndCards = message.split(thinsp + thinsp + n + thinsp + thinsp + n);

        if (isEmpty(titleAndCards) || titleAndCards.length != 2) {
            log.warn("Couldn't convert message to cardSet. Will stop the import. Message: {}", message);
            return null;
        }

        String title = titleAndCards[0].replace(openQuote, "").replace(closeQuote, "");

        if (length(title) > 100) {
            log.warn("Couldn't convert message to cardSet. Title length is longer than 100. Will stop the import. Title: {}", title);
            return null;
        }

        String cardsStr = titleAndCards[1];

        String[] cardsArr = cardsStr.split(thinsp + thinsp + n);
        if (isEmpty(cardsArr)) {
            log.warn("Couldn't convert cards string to cards. Will stop the import. Cards string: {}", cardsStr);
            return null;
        }

        List<CardDto> cards = Stream.of(cardsArr)
                .map(cardStr -> {
                    String[] frontAndBack = cardStr.split(thinsp + mediumDash + thinsp);
                    if (isEmpty(frontAndBack) || frontAndBack.length != 2) {
                        log.warn("Couldn't convert card string to card. This card will be removed from the cardSet. Card string: {}", cardStr);
                        return null;
                    }
                    if (length(frontAndBack[0]) > 100 || length(frontAndBack[1]) > 100) {
                        log.warn("Couldn't convert card string to card. Front or back side is longer than 100. This card will be removed from the cardSet. Card string: {}", cardStr);
                        return null;
                    }

                    return CardDto.builder()
                            .frontSide(frontAndBack[0])
                            .backSide(frontAndBack[1])
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        if (CollectionUtils.isEmpty(cards)) {
            log.warn("Couldn't convert cards string to cards. The result is empty. Will stop the import. Cards string: {}", cardsStr);
            return null;
        }

        return CardSetDto.builder()
                .title(title)
                .cards(cards)
                .build();
    }
}