package com.sergio.memo_bot.command_handler.exercise.connect_words.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ConnectWordsData {

    private List<WordWithHiddenAnswer> wordsWithHiddenAnswers;
    private List<ConnectedPair> connectedPairs;
    private int mistakeCount;

    private boolean pageable;
    private int totalNumberOfPages;
    private int currentPage;
}
