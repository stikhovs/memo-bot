package com.sergio.memo_bot.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record CardSetDto(Long id,
                         String title,
                         Integer userId,
                         UUID uuid,
                         List<CardDto> cards) {
}
