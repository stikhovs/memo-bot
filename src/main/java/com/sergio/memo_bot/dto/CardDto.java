package com.sergio.memo_bot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CardDto {
    private Long id;
    private String frontSide;
    private String backSide;
}
