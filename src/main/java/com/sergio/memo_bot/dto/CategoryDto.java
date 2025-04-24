package com.sergio.memo_bot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CategoryDto {
    private Long id;
    private String title;
    private boolean isDefault;
    private Integer userId;
}
