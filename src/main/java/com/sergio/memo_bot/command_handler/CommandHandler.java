package com.sergio.memo_bot.command_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;

public interface CommandHandler {

    boolean canHandle(CommandType commandType);

    Reply getReply(ProcessableMessage processableMessage);

}
