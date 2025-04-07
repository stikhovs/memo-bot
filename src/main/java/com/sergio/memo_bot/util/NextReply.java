package com.sergio.memo_bot.util;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class NextReply {

    private CommandType nextCommand;

    private ProcessableMessage previousProcessableMessage;

}
