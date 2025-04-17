package com.sergio.memo_bot.util;

import com.sergio.memo_bot.command_handler.exercise.quiz.dto.QuizItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BotQuizReply implements Reply {

    private Long chatId;

    private QuizItem quiz;

    private int correctIndex;

}
