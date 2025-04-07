package com.sergio.memo_bot;

import com.sergio.memo_bot.configuration.BotProperties;
import com.sergio.memo_bot.service.UpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class MemoBotConsumer implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final BotProperties botProperties;
    private final UpdateService updateService;

    @Override
    public void consume(Update update) {
        updateService.process(update);
    }

    @Override
    public String getBotToken() {
        return botProperties.getApiKey();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }


}
