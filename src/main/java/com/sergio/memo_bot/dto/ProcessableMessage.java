package com.sergio.memo_bot.dto;

import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.state.UserStateType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProcessableMessage {

    private boolean processable;

    private Long userId;

    private Long chatId;

    private String username;

    private boolean fromPartReply;

    private String text;

    private UserStateType currentUserStateType;

    private Integer messageId;

}
