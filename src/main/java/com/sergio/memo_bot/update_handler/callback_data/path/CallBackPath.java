package com.sergio.memo_bot.update_handler.callback_data.path;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallBackPath {

    boolean canProcess(CallbackQuery callbackQuery);

    BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId);

}
