package com.sergio.memo_bot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TelegramClientConfig {

    @Bean
    public TelegramClient memoBotClient(BotProperties botProperties) {
        return new OkHttpTelegramClient(botProperties.getApiKey());
    }

}
