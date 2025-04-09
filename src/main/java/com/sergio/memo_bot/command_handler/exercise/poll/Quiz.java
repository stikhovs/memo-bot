package com.sergio.memo_bot.command_handler.exercise.poll;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.poll.dto.QuizData;
import com.sergio.memo_bot.command_handler.exercise.poll.dto.QuizItem;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Quiz implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.QUIZ == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        QuizData quizData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.QUIZ, QuizData.class);

        int currentIndex = quizData.getCurrentIndex();

        if (currentIndex == quizData.getTotalNumberOfItems()) {
            CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);

            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text("Квиз завершен!")
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage.toBuilder().text(CommandType.GET_CARD_SET_INFO.getCommandText().formatted(cardSetDto.getId())).build())
                            .nextCommand(CommandType.GET_CARD_SET_INFO)
                            .build())
                    .build();
        } else {
            QuizItem quiz = quizData.getQuizItems().get(currentIndex);

            chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                    .chatId(processableMessage.getChatId())
                    .command(CommandType.QUIZ)
                    .data(new Gson().toJson(quizData.toBuilder()
                            .currentIndex(currentIndex + 1)
                            .build()))
                    .build());

            return BotQuizReply.builder()
                    .chatId(processableMessage.getChatId())
                    .quiz(quiz)
                    .correctIndex(getCorrectIndex(quiz.getAnswerOptions()))
                    .build();
        }
    }

    private int getCorrectIndex(List<Pair<String, Boolean>> answerOptions) {
        int correctIndex = -1;
        for (int i = 0; i < answerOptions.size(); i++) {
            if (answerOptions.get(i).getSecond()) {
                return i;
            }
        }
        return correctIndex;
    }

}
