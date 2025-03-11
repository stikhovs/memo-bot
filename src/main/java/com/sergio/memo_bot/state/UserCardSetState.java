package com.sergio.memo_bot.state;

import com.sergio.memo_bot.dto.CardSetDto;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserCardSetState {
    private final ConcurrentHashMap<Long, CardSetDto> userCardSet = new ConcurrentHashMap<>();

    public void setUserCardSet(Long userId, CardSetDto cardSetDto) {
        userCardSet.put(userId, cardSetDto);
    }

    public Optional<CardSetDto> getUserCardSet(Long userId) {
        return Optional.ofNullable(userCardSet.get(userId));
    }

    public void clearUserCardSet(Long userId) {
        userCardSet.remove(userId);
    }

}
