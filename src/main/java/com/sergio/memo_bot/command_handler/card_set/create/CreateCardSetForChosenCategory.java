package com.sergio.memo_bot.command_handler.card_set.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCardSetForChosenCategory implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CREATE_SET_FOR_CHOSEN_CATEGORY == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.SET_CREATION_START)
                .build();
    }
}