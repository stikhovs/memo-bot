package com.sergio.memo_bot.command_handler.exercise.answer_input.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class AnswerInputData {

    private int totalNumberOfItems;
    private int currentIndex;
    private List<AnswerInputItem> answerInputItems;

    private boolean pageable;
    private int totalNumberOfPages;
    private int currentPage;

}
