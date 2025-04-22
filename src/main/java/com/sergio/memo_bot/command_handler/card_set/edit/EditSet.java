package com.sergio.memo_bot.command_handler.card_set.edit;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class EditSet implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_SET == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(WHAT_DO_YOU_WANT_TO_EDIT)
                .replyMarkup(
                        InlineKeyboardMarkup.builder()
                                .keyboard(buildRows(cardSetDto))
                                .build()
                )
                .build();
    }

    private List<InlineKeyboardRow> buildRows(CardSetDto cardSetDto) {
        ArrayList<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text(RENAME_CARD_SET.formatted(cardSetDto.getTitle()))
                .callbackData(CommandType.EDIT_TITLE_REQUEST.getCommandText())
                .build()
        ));
        rows.addAll(
                cardSetDto
                        .getCards()
                        .stream()
                        .map(it ->
                                new InlineKeyboardRow(
                                        InlineKeyboardButton.builder()
                                                .text(EDIT_CARD.formatted(it.getFrontSide(), it.getBackSide()))
                                                .callbackData(CommandType.EDIT_CARD_REQUEST.getCommandText().formatted(it.getId()))
                                                .build()
                                ))
                        .toList()
        );
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text(ADD_CARD)
                .callbackData(CommandType.ADD_CARD_REQUEST.getCommandText())
                .build()
        ));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text(MOVE_CARD_SET_TO_ANOTHER_CATEGORY)
                .callbackData(CommandType.MOVE_SET_TO_ANOTHER_CATEGORY.getCommandText())
                .build()
        ));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text(DELETE_CARD_SET)
                .callbackData(CommandType.REMOVE_SET_REQUEST.getCommandText())
                .build()
        ));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text(BACK)
                .callbackData(CommandType.GET_CARD_SET_INFO.getCommandText().formatted(cardSetDto.getId()))
                .build()));
        return rows;
    }
}
