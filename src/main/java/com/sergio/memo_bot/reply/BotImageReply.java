package com.sergio.memo_bot.reply;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.InputStream;

@Data
@Builder(toBuilder = true)
public class BotImageReply implements Reply {

    private Long chatId;

    private InputStream image;

    private String fileName;

    private String caption;

    private ReplyKeyboard replyMarkup;

    private String parseMode;
}
