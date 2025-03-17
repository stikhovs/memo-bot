package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.*;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.UpdateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.state.UserStateType.FRONT_SIDE_CARD_ACCEPT;

@Slf4j
@Component
@RequiredArgsConstructor
public class _5_FrontSideCardAccept extends AbstractProcessable {
    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;
    private final UserInputState userInputState;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return FRONT_SIDE_CARD_ACCEPT == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        userInputState.setUserInputState(userId, true);
        if (processableMessage.isProcessable()) {
            userCardSetState.getUserCardSet(userId)
                    .ifPresent(cardSetDto -> cardSetDto
                            .getCards().add(
                                    CardDto.builder()
                                            .frontSide(userMessageHolder.getUserMessage(userId))
                                            .build()
                            ));
            log.info("Добавлена передняя сторона: {}", userCardSetState.getUserCardSet(processableMessage.getUserId()));
        } else {
            log.info("Передняя сторона не добавлена");
        }
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Обратная сторона карточки:")
                .chatId(processableMessage.getChatId())
                .build();
    }

    /*@Override
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
    }*/
}
