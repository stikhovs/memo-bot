package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserStateHolder;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.BaseProcessor;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.state.CommandType.DECLINE_SET_NAME;
import static com.sergio.memo_bot.state.UserStateType.CREATE_SET;
import static com.sergio.memo_bot.state.UserStateType.SET_NAME_DECLINE;

@Slf4j
//@Component
@RequiredArgsConstructor
public class _2_GiveNameToSetDecline extends BaseProcessor {

    private final UserMessageHolder userMessageHolder;
    private final UserStateHolder userStateHolder;
    private final _1_CreateSet createSetHandler;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return SET_NAME_DECLINE == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        userMessageHolder.clear(userId);
        userStateHolder.setUserState(userId, CREATE_SET);
        return createSetHandler.process(ProcessableMessage.builder()
                        .userId(userId)
                        .chatId(processableMessage.getChatId())
                        .currentUserStateType(CREATE_SET)
                        .text(DECLINE_SET_NAME.getCommandText())
                        .build())
                .toBuilder()
                .type(BotReplyType.EDIT_MESSAGE_TEXT)
                .messageId(processableMessage.getMessageId())
                .build();
    }

}
