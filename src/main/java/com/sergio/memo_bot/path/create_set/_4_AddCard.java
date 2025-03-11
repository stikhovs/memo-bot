package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.state.PathState;
import com.sergio.memo_bot.state.UserPathState;
import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import com.sergio.memo_bot.update_handler.text.path.TextPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
@RequiredArgsConstructor
public class _4_AddCard implements CallBackPath, TextPath {

    private final UserPathState userPathState;

    @Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return PathState.CARD_CREATION_COMPLETED == userPathState.getUserState(callbackQuery.getFrom().getId())
                && callbackQuery.getData().equals("Add one more card");
    }

    @Override
    public boolean canProcess(Message message) {
        return PathState.CARD_CREATION_BEGIN == userPathState.getUserState(message.getFrom().getId())
                && message.getText().equals("Прекрасно! Теперь давайте добавим карточки");
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        Long userId = callbackQuery.getFrom().getId();
        return sendMessage(userId, chatId);
    }

    @Override
    public BotApiMethodMessage process(Message message, Long chatId) {
        Long userId = message.getFrom().getId();
        return sendMessage(userId, chatId);
    }

    private SendMessage sendMessage(Long userId, Long chatId) {
        userPathState.setUserState(userId, PathState.CARD_CREATION_FRONT_SIDE);
        return SendMessage.builder()
                .text("Передняя сторона карточки:")
                .chatId(chatId)
                .build();
    }
}
