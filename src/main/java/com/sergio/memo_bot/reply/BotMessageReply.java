package com.sergio.memo_bot.reply;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Data
@Builder(toBuilder = true)
public class BotMessageReply implements Reply {

    private Long chatId;

    private String text;

    private String parseMode;

    private ReplyKeyboard replyMarkup;

    private NextReply nextReply;

}
