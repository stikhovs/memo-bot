package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.state.PathState;
import com.sergio.memo_bot.state.UserCardSetState;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserPathState;
import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Component
@RequiredArgsConstructor
public class _5_FrontSideCardAccept implements CallBackPath {

    private final UserPathState userPathState;
    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return userPathState.getUserState(callbackQuery.getFrom().getId()) == PathState.CARD_CREATION_FRONT_SIDE
                && callbackQuery.getData().equals("Yes");
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        Long userId = callbackQuery.getFrom().getId();
        userPathState.setUserState(userId, PathState.CARD_CREATION_BACK_SIDE);
        userCardSetState.getUserCardSet(userId)
                .ifPresent(cardSetDto -> cardSetDto
                        .getCards().add(
                                CardDto.builder()
                                        .frontSide(userMessageHolder.getUserMessage(userId))
                                        .build()
                        ));
        return SendMessage.builder()
                .text("Обратная сторона карточки:")
                .chatId(chatId)
                .build();
    }
}
