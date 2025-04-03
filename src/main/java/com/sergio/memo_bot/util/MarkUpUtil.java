package com.sergio.memo_bot.util;

import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.state.CommandType;
import org.apache.commons.lang3.tuple.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class MarkUpUtil {

    public static InlineKeyboardMarkup getInlineKeyboardMarkup(List<Pair<String, CommandType>> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(
                                buttons.stream()
                                        .map(button -> InlineKeyboardButton.builder()
                                                .text(button.getLeft())
                                                .callbackData(button.getRight().getCommandText())
                                                .build())
                                        .toList()
                        )
                ))
                .build();
    }

    public static ReplyKeyboardMarkup getReplyKeyboardMarkup(List<String> buttons) {
        return ReplyKeyboardMarkup.builder()
                .keyboard(buttons.stream()
                        .map(button -> new KeyboardRow(
                                KeyboardButton
                                        .builder()
                                        .text(button)
                                        .build()
                        ))
                        .toList())
                .build();
    }

    public static InlineKeyboardMarkup getInlineKeyboardMarkupRows(List<Pair<String, CommandType>> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(
                        buttons.stream()
                                .map(button -> new InlineKeyboardRow(
                                        List.of(InlineKeyboardButton.builder()
                                                .text(button.getLeft())
                                                .callbackData(button.getRight().getCommandText())
                                                .build()
                                        )))
                                .toList()
                )
                .build();
    }

    public static InlineKeyboardMarkup getInlineCardSetButtons(List<CardSetDto> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(
                        buttons.stream()
                                .map(button -> new InlineKeyboardRow(
                                        List.of(InlineKeyboardButton.builder()
                                                .text(button.getTitle())
                                                .callbackData(CommandType.GET_CARD_SET_INFO.getCommandText().formatted(button.getId()))
                                                .build()
                                        )))
                                .toList()
                )
                .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder().text("Назад").callbackData(CommandType.MAIN_MENU.getCommandText()).build()))
                .build();
    }

    public static ForceReplyKeyboard getForceReplyMarkup(String placeholder) {
        return ForceReplyKeyboard.builder()
                .forceReply(true)
                .selective(false)
                .inputFieldPlaceholder(placeholder)
                .build();
    }

}
