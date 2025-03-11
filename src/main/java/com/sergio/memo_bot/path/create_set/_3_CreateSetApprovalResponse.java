package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.state.PathState;
import com.sergio.memo_bot.state.UserCardSetState;
import com.sergio.memo_bot.state.UserPathState;
import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class _3_CreateSetApprovalResponse implements CallBackPath {

    private final UserPathState userPathState;
    private final UserCardSetState userCardSetState;

    @Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return callbackQuery.getData().equals("Accept set naming")
                && userPathState.getUserState(callbackQuery.getFrom().getId()) == PathState.SET_NAMING_APPROVAL;
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        Long userId = callbackQuery.getFrom().getId();
        userPathState.setUserState(userId, PathState.CARD_CREATION_BEGIN);
        userCardSetState.setUserCardSet(userId, CardSetDto.builder()
                .title(callbackQuery.getData())
                .cards(new ArrayList<>())
                .uuid(UUID.randomUUID())
                .build());

        return SendMessage.builder()
                .text("Прекрасно! Теперь давайте добавим карточки")
                .chatId(chatId)
                .build();
    }
}
