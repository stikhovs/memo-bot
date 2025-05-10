package com.sergio.memo_bot.command_handler.card_set.export_card_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.EmojiConverter;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.BACK;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportCardSetResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXPORT_CARD_SET_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(prepareExportMessage(cardSetDto))
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of(BACK, CommandType.GET_CARD_SET_INFO)
                )))
                .build();
    }

    private String prepareExportMessage(CardSetDto cardSetDto) {
        String n = "\n";
        String openQuote = EmojiConverter.getEmoji("U+201E");
        String closeQuote = EmojiConverter.getEmoji("U+201C");
        String thinsp = EmojiConverter.getEmoji("U+2009");
        String mediumDash = EmojiConverter.getEmoji("U+2013");

        String title = cardSetDto.getTitle();
        String cards = cardSetDto.getCards().stream().map(cardDto -> {
            String frontSide = cardDto.getFrontSide();
            String backSide = cardDto.getBackSide();
            return frontSide + thinsp + mediumDash + thinsp + backSide;
        }).collect(Collectors.joining(thinsp + thinsp + n));

        return openQuote + title + closeQuote + thinsp + thinsp
                + n + thinsp + thinsp
                + n
                + cards;
    }
}