package com.sergio.memo_bot.state;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserInputState {

    private final ConcurrentHashMap<Long, Boolean> userInputState = new ConcurrentHashMap<>();

    public void setUserInputState(Long userId, Boolean awaitsInput) {
        userInputState.put(userId, awaitsInput);
    }

    public Boolean getUserInputState(Long userId) {
        return userInputState.getOrDefault(userId, false);
    }

    public void clear(Long userId) {
        userInputState.remove(userId);
    }

}
