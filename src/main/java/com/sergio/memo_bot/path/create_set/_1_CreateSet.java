package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.state.PathState;
import com.sergio.memo_bot.state.UserPathState;
import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Component
@RequiredArgsConstructor
public class _1_CreateSet implements CallBackPath {
    private static final String CREATE_SET = "Create set";
    private final UserPathState userPathState;

    @Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return StringUtils.equalsIgnoreCase(CREATE_SET, callbackQuery.getData());
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        userPathState.setUserState(callbackQuery.getFrom().getId(), PathState.SET_NAMING);
        return SendMessage.builder()
                .text("Как будет называться ваш новый набор?")
                .chatId(chatId)
                .build();
    }
}
