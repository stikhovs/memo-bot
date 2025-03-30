package com.sergio.memo_bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class Sender {
    private final TelegramClient memoBotClient;

    public <T extends Serializable> T send(BotApiMethod<T> reply) {
        try {
            return memoBotClient.execute(reply);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
