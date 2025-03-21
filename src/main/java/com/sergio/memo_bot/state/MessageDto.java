package com.sergio.memo_bot.state;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MessageDto {
    private final Integer messageId;
    private final String messageText;
}
