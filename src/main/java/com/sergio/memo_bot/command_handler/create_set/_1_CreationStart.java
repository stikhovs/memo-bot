package com.sergio.memo_bot.command_handler.create_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class _1_CreationStart implements CommandHandler {

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SET_CREATION_START == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.SET_CATEGORY_REQUEST)
                .build();
    }
}