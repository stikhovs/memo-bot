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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        ExerciseData exerciseData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.EXERCISES_RESPONSE, ExerciseData.class);

        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<WordWithHiddenAnswer> wordsWithHiddenAnswers = exerciseData.getCards().stream().limit(50).flatMap(card -> Stream.of(
                WordWithHiddenAnswer.builder().wordToShow(card.getFrontSide()).hiddenAnswer(card.getBackSide()).build(),
                WordWithHiddenAnswer.builder().wordToShow(card.getBackSide()).hiddenAnswer(card.getFrontSide()).build()
        )).peek(wordWithHiddenAnswer -> wordWithHiddenAnswer.setId(atomicInteger.getAndIncrement())).collect(Collectors.toList());

        Collections.shuffle(wordsWithHiddenAnswers);

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                        .chatId(processableMessage.getChatId())
                        .command(CommandType.CONNECT_WORDS_REQUEST)
                        .data(new Gson().toJson(ConnectWordsData.builder()
                                        .connectedPairs(List.of())
                                        .wordsWithHiddenAnswers(wordsWithHiddenAnswers)
                                .build()))
                .build());

        return BotPartReply.builder()
                .nextCommand(CommandType.CONNECT_WORDS_REQUEST)
                .previousProcessableMessage(processableMessage)
                .build();
    }
}
