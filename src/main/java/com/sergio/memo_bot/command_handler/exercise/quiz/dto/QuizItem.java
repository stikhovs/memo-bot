package com.sergio.memo_bot.command_handler.exercise.quiz.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class QuizItem {

    private final String question;
    private final List<Pair<String, Boolean>> answerOptions;

}
