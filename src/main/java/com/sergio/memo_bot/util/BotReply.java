package com.sergio.memo_bot.util;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Data
@Builder
public class BotReply {

    private Long chatId;

    private String text;

    private ReplyKeyboard replyMarkup;

    private BotReplyType type;

    private BotReply nextReply;

}
