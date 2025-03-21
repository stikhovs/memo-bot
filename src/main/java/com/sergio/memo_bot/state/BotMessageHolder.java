package com.sergio.memo_bot.state;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class BotMessageHolder {
    private final ConcurrentHashMap<Long, Integer> botMessages = new ConcurrentHashMap<>();

    public void setBotMessage(Long userId, Integer messageId) {
        botMessages.put(userId, messageId);
    }

    public Integer getBotMessage(Long userId) {
        return botMessages.get(userId);
    }

    public void clear(Long userId) {
        botMessages.remove(userId);
    }

}
