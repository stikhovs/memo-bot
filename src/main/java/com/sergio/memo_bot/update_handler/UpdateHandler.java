package com.sergio.memo_bot.update_handler;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;

public interface UpdateHandler {

    boolean canHandle(Update update);

    BotApiMethodMessage handle(Update update);

}
