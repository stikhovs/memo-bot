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
import java.util.Optional;
import java.util.stream.Collectors;

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
        Long chatId = processableMessage.getChatId();
        ExerciseData exerciseData = chatTempDataService.mapDataToType(chatId, CommandType.EXERCISES_RESPONSE, ExerciseData.class);

        if (!exerciseData.isUsePagination() && size(exerciseData.getCards()) < 2) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(THERE_SHOULD_BE_TWO_OR_MORE_CARDS_FOR_QUIZ)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                            .build())
                    .build();
        }

        if (exerciseData.isUsePagination()) {
            Optional<ChatTempData> chatTempData = chatTempDataService.find(chatId, CommandType.QUIZ);
            if (chatTempData.isPresent()) {
                QuizData quizData = new Gson().fromJson(chatTempData.get().getData(), QuizData.class);
                int nextPageIndex = quizData.getCurrentPage() + 1;
                List<QuizItem> quizItems = extractQuizItemsFromPages(exerciseData, nextPageIndex);
                saveDataForPages(chatId, quizItems, size(exerciseData.getCardPages()), nextPageIndex);
            } else {
                // it's the first time - page = 0
                List<QuizItem> quizItems = extractQuizItemsFromPages(exerciseData, 0);
                saveDataForPages(chatId, quizItems, size(exerciseData.getCardPages()), 0);
            }
        } else {
            // no pages
            List<QuizItem> quizItems = extractQuizItemsFromAllCards(exerciseData);
            saveDataWithoutPages(chatId, quizItems);
        }

        return BotPartReply.builder()
                .chatId(chatId)
                .nextCommand(CommandType.QUIZ)
                .previousProcessableMessage(processableMessage)
                .build();
    }

    private void saveDataForPages(Long chatId, List<QuizItem> quizItems, int totalNumberOfPages, int currentPage) {
        saveData(chatId, quizItems, true, totalNumberOfPages, currentPage);
    }

    private void saveDataWithoutPages(Long chatId, List<QuizItem> quizItems) {
        saveData(chatId, quizItems, false, 0, 0);
    }

    private void saveData(Long chatId, List<QuizItem> quizItems, boolean pageable, int totalNumberOfPages, int currentPage) {
        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.QUIZ)
                .data(new Gson().toJson(QuizData.builder()
                        .quizItems(quizItems)
                        .totalNumberOfItems(quizItems.size())
                        .currentIndex(0)
                        .pageable(pageable)
                        .totalNumberOfPages(totalNumberOfPages)
                        .currentPage(currentPage)
                        .build()))
                .build());
    }

    private List<QuizItem> extractQuizItemsFromAllCards(ExerciseData exerciseData) {
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

    private List<QuizItem> extractQuizItemsFromPages(ExerciseData exerciseData, int pageIndex) {
        List<CardDto> cards = exerciseData.getCardPages().get(pageIndex);
        ArrayList<QuizItem> result = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            List<Pair<String, Boolean>> answerOptions = new ArrayList<>();

            String correctAnswer = cards.get(i).getBackSide();
            answerOptions.add(Pair.of(correctAnswer, true));

            putIncorrectOptions(cards, correctAnswer, answerOptions);

            if (answerOptions.size() < 2) {
                putIncorrectOptions(exerciseData.getCards(), correctAnswer, answerOptions);
            }

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
                .collect(Collectors.toList());

        // shuffle incorrect options
        Collections.shuffle(excludedCorrectAnswer);

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
