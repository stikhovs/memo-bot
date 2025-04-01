package com.sergio.memo_bot.util;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Data
@Builder(toBuilder = true)
public class MultipleBotReply implements Reply {

    private Long chatId;

    private String text;

    private boolean isFinal;

    private CommandType nextCommand;

    private ReplyKeyboard replyMarkup;

    private ProcessableMessage previousProcessableMessage;

    private BotReplyType type;

    private Integer messageId;

    private String parseMode;

}
