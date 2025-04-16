package com.sergio.memo_bot.command_handler.category.add_sets_to_category.dto;

import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class AddSetToCategoryData {

    private CategoryDto category;
    private List<CardSetDto> allCardSets;
    private List<CardSetDto> chosenCardSets;

}
