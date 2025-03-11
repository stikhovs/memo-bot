package com.sergio.memo_bot.state;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserPathState {
    private final ConcurrentHashMap<Long, PathState> userStates = new ConcurrentHashMap<>();

    public void setUserState(Long userId, PathState pathState) {
        userStates.put(userId, pathState);
    }

    public PathState getUserState(Long userId) {
        return userStates.getOrDefault(userId, PathState.DEFAULT);
    }

    public void clearUserState(Long userId) {
        userStates.remove(userId);
    }
}
