package com.sergio.memo_bot.dto;

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

    private Integer messageId;

}
