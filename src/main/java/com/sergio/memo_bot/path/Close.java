package com.sergio.memo_bot.path;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import org.springframework.stereotype.Service;

@Service
public class Close extends AbstractProcessable {
    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return userStateType == UserStateType.CLOSE;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        return BotReply.builder()
                .type(BotReplyType.DELETE_MESSAGE_TEXT)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .build();
    }
}
