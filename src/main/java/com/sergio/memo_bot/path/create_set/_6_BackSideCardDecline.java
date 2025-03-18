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

import static com.sergio.memo_bot.state.CommandType.DECLINE_BACK_SIDE;
import static com.sergio.memo_bot.state.UserStateType.BACK_SIDE_CARD_DECLINE;
import static com.sergio.memo_bot.state.UserStateType.FRONT_SIDE_CARD_ACCEPT;

@Slf4j
@Component
@RequiredArgsConstructor
public class _6_BackSideCardDecline extends AbstractProcessable {
    private final UserMessageHolder userMessageHolder;
    private final UserStateHolder userStateHolder;
    private final _5_FrontSideCardAccept frontSideCardAccept;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return BACK_SIDE_CARD_DECLINE == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        userMessageHolder.clear(userId);
        userStateHolder.setUserState(userId, FRONT_SIDE_CARD_ACCEPT);
        return frontSideCardAccept.process(ProcessableMessage.builder()
                .userId(userId)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .currentUserStateType(FRONT_SIDE_CARD_ACCEPT)
                .text(DECLINE_BACK_SIDE.getCommandText())
                .build());
    }

}
