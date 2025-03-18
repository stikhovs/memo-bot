package com.sergio.memo_bot.util;

import com.sergio.memo_bot.state.CommandType;
import org.apache.commons.lang3.tuple.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;

public class MarkUpUtil {

    public static InlineKeyboardMarkup getInlineKeyboardMarkup(List<Pair<String, CommandType>> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        buttons.stream()
                                .map(button -> InlineKeyboardButton.builder()
                                        .text(button.getLeft())
                                        .callbackData(button.getRight().getCommandText())
                                        .build())
                                .toList()
                ))
                .build();
    }

    public static InlineKeyboardMarkup getInlineKeyboardMarkupRows(List<Pair<String, CommandType>> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(
                        buttons.stream()
                                .map(button -> List.of(InlineKeyboardButton.builder()
                                        .text(button.getLeft())
                                        .callbackData(button.getRight().getCommandText())
                                        .build()))
                                .toList()
                )
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
