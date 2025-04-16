package com.sergio.memo_bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class CardSetDto {
    private Long id;
    private String title;
    private Long telegramChatId;
    private Long categoryId;
    private UUID uuid;
    private List<CardDto> cards;
}
