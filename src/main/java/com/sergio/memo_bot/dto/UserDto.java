package com.sergio.memo_bot.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserDto(String username,
                      Long telegramUserId,
                      Long telegramChatId) {

}
