package com.sergio.memo_bot.reply;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BotPartReply implements Reply {

    private Long chatId;

    private String text;

    private ProcessableMessage previousProcessableMessage;

    private CommandType nextCommand;

}
