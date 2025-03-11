package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.state.PathState;
import com.sergio.memo_bot.state.UserCardSetState;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserPathState;
import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import com.sergio.memo_bot.util.EmojiConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class _6_BackSideCardAccept implements CallBackPath {

    private final UserPathState userPathState;
    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return userPathState.getUserState(callbackQuery.getFrom().getId()) == PathState.CARD_CREATION_BACK_SIDE
                && callbackQuery.getData().equals("Yes");
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        Long userId = callbackQuery.getFrom().getId();
        userPathState.setUserState(userId, PathState.CARD_CREATION_COMPLETED);
        userCardSetState.getUserCardSet(userId).flatMap(cardSetDto -> cardSetDto
                .getCards()
                .stream()
                .filter(cardDto -> isNotBlank(cardDto.getFrontSide()) && isBlank(cardDto.getBackSide()))
                .findFirst()).ifPresent(card -> card.setBackSide(userMessageHolder.getUserMessage(userId))
        );

//        System.out.println(userCardSetState.getUserCardSet(userId));

        return SendMessage.builder().chatId(chatId)
                .text("Отлично! Добавить еще карточку?")
                .replyMarkup(getInlineKeyboardMarkup())
                .build();
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder().text(EmojiConverter.getEmoji("U+2705") + " Да").callbackData("Add one more card").build(),
                        InlineKeyboardButton.builder().text(EmojiConverter.getEmoji("U+274C") + " Нет, сохранить набор").callbackData("Save cardSet").build()
                ))
                .build();
    }
}
