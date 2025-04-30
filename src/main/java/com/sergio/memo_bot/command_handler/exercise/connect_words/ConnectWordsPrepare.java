package com.sergio.memo_bot.command_handler.exercise.connect_words;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.ExerciseData;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.ConnectWordsData;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.WordWithHiddenAnswer;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectWordsPrepare implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CONNECT_WORDS_PREPARE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        ExerciseData exerciseData = chatTempDataService.mapDataToType(chatId, CommandType.EXERCISES_RESPONSE, ExerciseData.class);

        if (exerciseData.isUsePagination()) {
            Optional<ChatTempData> optionalConnectWordsData = chatTempDataService.find(chatId, CommandType.CONNECT_WORDS_REQUEST);
            if (optionalConnectWordsData.isPresent()) {
                ConnectWordsData connectWordsData = new Gson().fromJson(optionalConnectWordsData.get().getData(), ConnectWordsData.class);
                int nextPageIndex = connectWordsData.getCurrentPage() + 1;
                List<WordWithHiddenAnswer> wordWithHiddenAnswers = extractWordsFromPages(exerciseData, nextPageIndex);
                saveDataForPages(chatId, wordWithHiddenAnswers,  size(exerciseData.getCardPages()), nextPageIndex);
            } else {
                // it's the first time - page = 0
                List<WordWithHiddenAnswer> wordWithHiddenAnswers = extractWordsFromPages(exerciseData, 0);
                saveDataForPages(chatId, wordWithHiddenAnswers,  size(exerciseData.getCardPages()), 0);
            }
        } else {
            // no pages
            List<WordWithHiddenAnswer> wordWithHiddenAnswers = extractWordsFromAllCards(exerciseData);
            saveDataWithoutPages(chatId, wordWithHiddenAnswers);
        }

        return BotPartReply.builder()
                .nextCommand(CommandType.CONNECT_WORDS_REQUEST)
                .previousProcessableMessage(processableMessage)
                .build();
    }

    private void saveDataForPages(Long chatId, List<WordWithHiddenAnswer> wordsWithHiddenAnswers, int totalNumberOfPages, int currentPage) {
        saveData(chatId, wordsWithHiddenAnswers, true, totalNumberOfPages, currentPage);
    }

    private void saveDataWithoutPages(Long chatId, List<WordWithHiddenAnswer> wordsWithHiddenAnswers) {
        saveData(chatId, wordsWithHiddenAnswers, false, 0, 0);
    }

    private void saveData(Long chatId, List<WordWithHiddenAnswer> wordsWithHiddenAnswers, boolean pageable, int totalNumberOfPages, int currentPage) {
        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.CONNECT_WORDS_REQUEST)
                .data(new Gson().toJson(ConnectWordsData.builder()
                        .connectedPairs(List.of())
                        .wordsWithHiddenAnswers(wordsWithHiddenAnswers)
                        .pageable(pageable)
                        .totalNumberOfPages(totalNumberOfPages)
                        .currentPage(currentPage)
                        .build()))
                .build());
    }

    private List<WordWithHiddenAnswer> extractWordsFromAllCards(ExerciseData exerciseData) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<WordWithHiddenAnswer> wordsWithHiddenAnswers = exerciseData.getCards().stream()
                .flatMap(card -> Stream.of(
                        WordWithHiddenAnswer.builder().wordToShow(card.getFrontSide()).hiddenAnswer(card.getBackSide()).build(),
                        WordWithHiddenAnswer.builder().wordToShow(card.getBackSide()).hiddenAnswer(card.getFrontSide()).build()
                )).peek(wordWithHiddenAnswer -> wordWithHiddenAnswer.setId(atomicInteger.getAndIncrement()))
                .collect(Collectors.toList());
        Collections.shuffle(wordsWithHiddenAnswers);
        return wordsWithHiddenAnswers;
    }

    private List<WordWithHiddenAnswer> extractWordsFromPages(ExerciseData exerciseData, int pageIndex) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<WordWithHiddenAnswer> wordsWithHiddenAnswers = exerciseData.getCardPages().get(pageIndex).stream()
                .flatMap(card -> Stream.of(
                        WordWithHiddenAnswer.builder().wordToShow(card.getFrontSide()).hiddenAnswer(card.getBackSide()).build(),
                        WordWithHiddenAnswer.builder().wordToShow(card.getBackSide()).hiddenAnswer(card.getFrontSide()).build()
                )).peek(wordWithHiddenAnswer -> wordWithHiddenAnswer.setId(atomicInteger.getAndIncrement()))
                .collect(Collectors.toList());
        Collections.shuffle(wordsWithHiddenAnswers);
        return wordsWithHiddenAnswers;
    }
}
