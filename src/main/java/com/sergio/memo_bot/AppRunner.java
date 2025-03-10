package com.sergio.memo_bot;

import com.sergio.memo_bot.configuration.BotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class AppRunner implements ApplicationRunner {

    private final BotProperties botProperties;
    private final BotListener botListener;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(botProperties.getApiKey());
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(botListener);
    }
}
