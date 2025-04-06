package com.sergio.memo_bot.service;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

@Data
@Builder
public class ReplyData {
    private final BotApiMethod<?> reply;
    private final boolean hasButtons;
    private final Long chatId;
    private final Integer messageId;
}
