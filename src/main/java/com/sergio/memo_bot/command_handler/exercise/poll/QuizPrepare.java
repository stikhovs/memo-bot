package com.sergio.memo_bot.command_handler.exercise.poll;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.poll.dto.QuizData;
import com.sergio.memo_bot.command_handler.exercise.poll.dto.QuizItem;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.NextReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);

        if (CollectionUtils.size(cardSetDto.getCards()) < 2) {
            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text("Для квиза в наборе должно быть 2 и более карточек")
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage.toBuilder().text(CommandType.GET_CARD_SET_INFO.getCommandText().formatted(cardSetDto.getId())).build())
                            .nextCommand(CommandType.GET_CARD_SET_INFO)
                            .build())
                    .build();
        }

        List<QuizItem> preparedQuiz = prepare(cardSetDto);

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

    private List<QuizItem> prepare(CardSetDto cardSetDto) {
        List<CardDto> cards = cardSetDto.getCards();
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
                .collect(Collectors.toList());

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
