package com.sergio.memo_bot.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class CardSetDto {
    private String title;
    private Integer userId;
    private UUID uuid;
    private List<CardDto> cards;
}
