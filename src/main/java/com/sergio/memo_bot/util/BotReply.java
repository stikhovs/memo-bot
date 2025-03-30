package com.sergio.memo_bot.util;

import com.sergio.memo_bot.dto.ProcessableMessage;
import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Data
@Builder(toBuilder = true)
public class BotReply implements Reply {

    private Long chatId;

    private String text;

    private ReplyKeyboard replyMarkup;

    private BotReplyType type;

    private Reply nextReply;

    private Integer messageId;

}
