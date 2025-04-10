package com.sergio.memo_bot.command_handler.exercise.connect_words.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ConnectedPair {

    private String wordOne;
    private String wordTwo;

}
