package com.sergio.memo_bot.update_handler.text.path;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface TextPath {

    boolean canProcess(Message message);

    BotApiMethodMessage process(Message message, Long chatId);

}
