package com.sergio.memo_bot.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public class CardSetDto {
    private String title;
    private Long telegramChatId;
    private UUID uuid;
    private List<CardDto> cards;
}
