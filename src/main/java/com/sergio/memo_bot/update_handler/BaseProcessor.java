package com.sergio.memo_bot.update_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class BaseProcessor {

    public abstract boolean canHandleByUserState(UserStateType userStateType);

    public boolean canHandleByUserState(ProcessableMessage processableMessage) {
        return canHandleByUserState(processableMessage.getCurrentUserStateType());
    }

    public abstract BotReply process(ProcessableMessage processableMessage);

    public void replyTo(ProcessableMessage processableMessage, Consumer<BotApiMethod<?>> action) {
        log.info("User: [{}], Message: [{}], Message id: [{}], Current state: [{}], Chosen handler: [{}]",
                processableMessage.getUsername(),
                processableMessage.getText(),
                processableMessage.getMessageId(),
                processableMessage.getCurrentUserStateType(),
                this.getClass().getName());
        if (processableMessage.isProcessable()) {
            BotReply reply = process(processableMessage);
            send(reply, action);
        } else {
            log.error("Couldn't process processableMessage: [{}]", processableMessage);
            action.accept(
                    SendMessage.builder()
                            .text("Неизвестная команда")
                            .build()
            );
        }
    }

    private void send(BotReply reply, Consumer<BotApiMethod<?>> action) {
        /*if (reply != null) {
            action.accept(BotReplyMapper.toBotApiMethod(reply));
            if (reply.getNextReply() != null) {
                send(reply.getNextReply(), action);
            }
        }*/
    }


}
