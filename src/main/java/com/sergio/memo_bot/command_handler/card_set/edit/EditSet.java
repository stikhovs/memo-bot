package com.sergio.memo_bot.command_handler.card_set.edit;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

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
                .text("Что хотите отредактировать?")
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
                .text("Переименовать набор: %s".formatted(cardSetDto.getTitle()))
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
                                                .text("Изменить карточку: %s — %s".formatted(it.getFrontSide(), it.getBackSide()))
                                                .callbackData(CommandType.EDIT_CARD_REQUEST.getCommandText().formatted(it.getId()))
                                                .build()
                                ))
                        .toList()
        );
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Добавить карточку")
                .callbackData(CommandType.ADD_CARD_REQUEST.getCommandText())
                .build()
        ));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Перенести набор в другую категорию")
                .callbackData(CommandType.MOVE_SET_TO_ANOTHER_CATEGORY.getCommandText())
                .build()
        ));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Удалить набор")
                .callbackData(CommandType.REMOVE_SET_REQUEST.getCommandText())
                .build()
        ));
        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData(CommandType.GET_CARD_SET_INFO.getCommandText().formatted(cardSetDto.getId()))
                .build()));
        return rows;
    }
}
