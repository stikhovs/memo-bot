package com.sergio.memo_bot.command_handler.card.get;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.BACK;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCards implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_CARDS == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .parseMode(ParseMode.HTML)
                .text(
                        "<strong><u>%s</u></strong>\n\n".formatted(cardSetDto.getTitle())
                                + getCardsList(cardSetDto.getCards())
                )
                .replyMarkup(
                        InlineKeyboardMarkup.builder()
                                .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                        .text(BACK)
                                        .callbackData(CommandType.GET_CARD_SET_INFO.getCommandText().formatted(cardSetDto.getId()))
                                        .build()))
                                .build()
                )
                .build();
    }

    private String getCardsList(List<CardDto> cards) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            int currentNumber = i + 1;
            result.append(currentNumber).append(")").append(" ");

            CardDto card = cards.get(i);

            result.append(card.getFrontSide())
                    .append(" — ")
                    .append(card.getBackSide());
            result.append("\n\n");
        }
        return result.toString();
    }

}
