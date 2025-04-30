package com.sergio.memo_bot.command_handler.exercise;

import com.sergio.memo_bot.dto.CardDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ExerciseData {

    List<CardDto> cards;

    boolean usePagination;
    List<List<CardDto>> cardPages;

}
