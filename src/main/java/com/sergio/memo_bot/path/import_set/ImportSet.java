package com.sergio.memo_bot.path.import_set;

import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportSet implements CallBackPath {
    private static final String IMPORT_SET = "Import set";
    @Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return StringUtils.equalsIgnoreCase(IMPORT_SET, callbackQuery.getData());
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        return SendMessage.builder().text("Кликнули Импортировать сет").chatId(chatId).build();
    }
}
