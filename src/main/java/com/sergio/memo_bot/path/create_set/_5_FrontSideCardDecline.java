package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserStateHolder;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.BotReply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.state.CommandType.DECLINE_FRONT_SIDE;
import static com.sergio.memo_bot.state.UserStateType.ADD_CARD_REQUEST;
import static com.sergio.memo_bot.state.UserStateType.FRONT_SIDE_CARD_DECLINE;

@Slf4j
@Component
@RequiredArgsConstructor
public class _5_FrontSideCardDecline extends AbstractProcessable {
    private final UserMessageHolder userMessageHolder;
    private final UserStateHolder userStateHolder;
    private final _4_AddCard addCard;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return FRONT_SIDE_CARD_DECLINE == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        userMessageHolder.clear(userId);
        userStateHolder.setUserState(userId, ADD_CARD_REQUEST);
        return addCard.process(ProcessableMessage.builder()
                .userId(userId)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .currentUserStateType(ADD_CARD_REQUEST)
                .text(DECLINE_FRONT_SIDE.getCommandText())
                .build());
    }
}
