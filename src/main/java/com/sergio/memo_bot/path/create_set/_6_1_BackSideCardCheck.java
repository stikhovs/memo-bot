package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.state.PathState;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserPathState;
import com.sergio.memo_bot.update_handler.text.path.TextPath;
import com.sergio.memo_bot.util.EmojiConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class _6_1_BackSideCardCheck implements TextPath {

    private final UserPathState userPathState;
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canProcess(Message message) {
        return userPathState.getUserState(message.getFrom().getId()) == PathState.CARD_CREATION_BACK_SIDE;
    }

    @Override
    public BotApiMethodMessage process(Message message, Long chatId) {
        userMessageHolder.setUserMessage(message.getFrom().getId(), message.getText());
        return SendMessage.builder()
                .chatId(chatId)
                .text("Обратная сторона: %s\nВерно?".formatted(message.getText()))
                .replyMarkup(getInlineKeyboardMarkup())
                .build();
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder().text(EmojiConverter.getEmoji("U+2705") + " Да").callbackData("Yes").build(),
                        InlineKeyboardButton.builder().text(EmojiConverter.getEmoji("U+274C") + " Нет").callbackData("No").build()
                ))
                .build();
    }

}
