package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserInputState;
import com.sergio.memo_bot.state.UserStateHolder;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.UpdateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.state.UserStateType.CREATE_SET;

@Slf4j
@Component
@RequiredArgsConstructor
public class _1_CreateSet extends AbstractProcessable {

    private final UserInputState userInputState;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return CREATE_SET == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        userInputState.setUserInputState(processableMessage.getUserId(), true);
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Как будет называться ваш новый набор?")
                .chatId(processableMessage.getChatId())
                .build();
    }
}
