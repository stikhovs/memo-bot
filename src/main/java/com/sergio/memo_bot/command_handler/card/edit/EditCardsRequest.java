package com.sergio.memo_bot.command_handler.card.edit;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditCardsRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_CARDS_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);

        List<InlineKeyboardRow> rows = getRows(cardSetDto);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOOSE_CARD)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(rows)
                        .build())
                .build();
    }

    @NotNull
    private static List<InlineKeyboardRow> getRows(CardSetDto cardSetDto) {
        List<InlineKeyboardRow> cardButtons = cardSetDto
                .getCards()
                .stream()
                .map(it ->
                        new InlineKeyboardRow(
                                InlineKeyboardButton.builder()
                                        .text(EDIT_CARD.formatted(it.getFrontSide(), it.getBackSide()))
                                        .callbackData(CommandType.EDIT_CARD_REQUEST.getCommandText().formatted(it.getId()))
                                        .build()
                        ))
                .toList();

        InlineKeyboardRow backButton = new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text(BACK)
                        .callbackData(CommandType.EDIT_SET.getCommandText())
                        .build()
        );

        List<InlineKeyboardRow> result = new ArrayList<>(cardButtons);
        result.add(backButton);

        return result;
    }
}