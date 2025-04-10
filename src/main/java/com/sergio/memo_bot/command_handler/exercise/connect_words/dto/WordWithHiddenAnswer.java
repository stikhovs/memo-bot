package com.sergio.memo_bot.command_handler.exercise.connect_words.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class WordWithHiddenAnswer {

    private int id;
    private String wordToShow;
    private String hiddenAnswer;
    private boolean active;

}
