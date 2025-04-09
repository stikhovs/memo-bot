package com.sergio.memo_bot.command_handler.exercise.answer_input.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AnswerInputItem {

    private String question;
    private String correctAnswer;

}
