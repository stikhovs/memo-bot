package com.sergio.memo_bot.command_handler.exercise.quiz;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.ExerciseData;
import com.sergio.memo_bot.command_handler.exercise.quiz.dto.QuizData;
import com.sergio.memo_bot.command_handler.exercise.quiz.dto.QuizItem;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.THERE_SHOULD_BE_TWO_OR_MORE_CARDS_FOR_QUIZ;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.lang3.ObjectUtils.notEqual;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizPrepare implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.QUIZ_PREPARE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        ExerciseData exerciseData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.EXERCISES_RESPONSE, ExerciseData.class);

        if (size(exerciseData.getCards()) < 2) {
            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text(THERE_SHOULD_BE_TWO_OR_MORE_CARDS_FOR_QUIZ)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                            .build())
                    .build();
        }

        List<QuizItem> preparedQuiz = prepare(exerciseData);

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.QUIZ)
                .data(new Gson().toJson(QuizData.builder()
                        .quizItems(preparedQuiz)
                        .totalNumberOfItems(preparedQuiz.size())
                        .currentIndex(0)
                        .build()))
                .build());


        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .nextCommand(CommandType.QUIZ)
                .previousProcessableMessage(processableMessage)
                .build();
    }

    private List<QuizItem> prepare(ExerciseData exerciseData) {
        List<CardDto> cards = exerciseData.getCards();
        ArrayList<QuizItem> result = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            List<Pair<String, Boolean>> answerOptions = new ArrayList<>();

            String correctAnswer = cards.get(i).getBackSide();
            answerOptions.add(Pair.of(correctAnswer, true));

            putIncorrectOptions(cards, correctAnswer, answerOptions);

            // shuffle options
            Collections.shuffle(answerOptions);

            result.add(
                    QuizItem.builder()
                            .question(cards.get(i).getFrontSide())
                            .answerOptions(answerOptions)
                            .build()
            );
        }

        // shuffle questions
        Collections.shuffle(result);

        return result;
    }

    private void putIncorrectOptions(List<CardDto> cards, String correctAnswer, List<Pair<String, Boolean>> answerOptions) {
        List<CardDto> excludedCorrectAnswer = cards.stream()
                .filter(cardDto -> notEqual(cardDto.getBackSide(), correctAnswer))
                .toList();

        if (cards.size() > 3) {
            for (int i = 0; i < 3; i++) {
                answerOptions.add(Pair.of(excludedCorrectAnswer.get(i).getBackSide(), false));
            }
        } else {
            for (CardDto cardDto : excludedCorrectAnswer) {
                answerOptions.add(Pair.of(cardDto.getBackSide(), false));
            }
        }
    }
}
