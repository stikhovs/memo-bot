package com.sergio.memo_bot.command_handler.exercise.flash_card.dto;

import com.sergio.memo_bot.dto.CardDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class FlashCardData {

    private final int totalNumberOfCards;
    private final int currentIndex;
    private final CardDto card;
    private final VisibleSide visibleSide;

}
