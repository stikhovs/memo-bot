package com.sergio.memo_bot.path.show_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.BaseProcessor;
import com.sergio.memo_bot.util.BotReply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.state.UserStateType.SHOW_SET_REQUESTED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShowSet extends BaseProcessor {
    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return SHOW_SET_REQUESTED == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        return null;
    }
}
