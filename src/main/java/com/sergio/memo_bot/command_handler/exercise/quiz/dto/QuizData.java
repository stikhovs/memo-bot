package com.sergio.memo_bot.command_handler.exercise.quiz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class QuizData {

    private List<QuizItem> quizItems;
    private int totalNumberOfItems;
    private int currentIndex;

    private boolean pageable;
    private int totalNumberOfPages;
    private int currentPage;

}
