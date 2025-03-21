package com.sergio.memo_bot.state;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserMessageHolder {

    private final ConcurrentHashMap<Long, MessageDto> userMessages = new ConcurrentHashMap<>();

    public void setUserMessage(Long userId, MessageDto message) {
        userMessages.put(userId, message);
    }

    public MessageDto getUserMessage(Long userId) {
        return userMessages.get(userId);
    }

    public void clear(Long userId) {
        userMessages.remove(userId);
    }

}
