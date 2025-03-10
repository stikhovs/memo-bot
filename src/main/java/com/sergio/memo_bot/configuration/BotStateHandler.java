package com.sergio.memo_bot.configuration;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class BotStateHandler {
    private final ConcurrentHashMap<Long, State> userStates = new ConcurrentHashMap<>();

    public void setUserState(Long userId, State state) {
        userStates.put(userId, state);
    }

    public State getUserState(Long userId) {
        return userStates.getOrDefault(userId, State.DEFAULT);
    }

    public void clearUserState(Long userId) {
        userStates.remove(userId);
    }
}
