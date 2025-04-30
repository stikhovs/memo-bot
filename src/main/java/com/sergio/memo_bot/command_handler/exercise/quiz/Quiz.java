package com.sergio.memo_bot.command_handler.exercise.quiz;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.quiz.dto.QuizData;
import com.sergio.memo_bot.command_handler.exercise.quiz.dto.QuizItem;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotQuizReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

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
        Long chatId = processableMessage.getChatId();
        QuizData quizData = chatTempDataService.mapDataToType(chatId, CommandType.QUIZ, QuizData.class);

        int currentIndex = quizData.getCurrentIndex();

        if (currentIndex == quizData.getTotalNumberOfItems()) {
            if (quizData.isPageable()) {
                if (quizData.getCurrentPage() + 1 == quizData.getTotalNumberOfPages()) {
                    // if it was the last page
                    chatTempDataService.clear(chatId, CommandType.QUIZ);
                    return BotMessageReply.builder()
                            .chatId(chatId)
                            .text(QUIZ_FINISHED)
                            .nextReply(NextReply.builder()
                                    .previousProcessableMessage(processableMessage)
                                    .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                                    .build())
                            .build();
                } else {
                    return BotMessageReply.builder()
                            .chatId(chatId)
                            .text(LEVEL_FINISHED.formatted(quizData.getCurrentPage() + 1, quizData.getTotalNumberOfPages()))
                            .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                    org.apache.commons.lang3.tuple.Pair.of(LEAVE_LEVEL, CommandType.EXERCISES_DATA_PREPARE),
                                    org.apache.commons.lang3.tuple.Pair.of(NEXT_LEVEL, CommandType.QUIZ_PREPARE)
                            )))
                            .build();
                }
            } else {
                return BotMessageReply.builder()
                        .chatId(chatId)
                        .text(QUIZ_FINISHED)
                        .nextReply(NextReply.builder()
                                .previousProcessableMessage(processableMessage)
                                .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                                .build())
                        .build();
            }
        } else {
            QuizItem quiz = quizData.getQuizItems().get(currentIndex);

            chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                    .chatId(chatId)
                    .command(CommandType.QUIZ)
                    .data(new Gson().toJson(quizData.toBuilder()
                            .currentIndex(currentIndex + 1)
                            .build()))
                    .build());

            return BotQuizReply.builder()
                    .chatId(chatId)
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
