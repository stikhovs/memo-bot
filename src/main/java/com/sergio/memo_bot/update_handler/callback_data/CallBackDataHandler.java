package com.sergio.memo_bot.update_handler.callback_data;

import com.sergio.memo_bot.update_handler.UpdateHandler;
import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import com.sergio.memo_bot.update_handler.text.path.TextPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallBackDataHandler implements UpdateHandler {

    private final List<CallBackPath> paths;
    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery();
    }
    @Override
    public BotApiMethodMessage handle(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getFrom().getId();

        CallBackPath path = paths.stream()
                .filter(it -> it.canProcess(callbackQuery))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find the right path for callbackQuery: [%s]".formatted(callbackQuery)));

        return path.process(callbackQuery, chatId);
    }
}
