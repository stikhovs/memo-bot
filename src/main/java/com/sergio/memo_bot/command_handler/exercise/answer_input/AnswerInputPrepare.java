package com.sergio.memo_bot.command_handler.exercise.answer_input;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.ExerciseData;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputData;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputItem;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnswerInputPrepare implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.ANSWER_INPUT_PREPARE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        ExerciseData exerciseData = chatTempDataService.mapDataToType(chatId, CommandType.EXERCISES_RESPONSE, ExerciseData.class);

        if (exerciseData.isUsePagination()) {
            Optional<ChatTempData> optionalAnswerInputData = chatTempDataService.find(chatId, CommandType.ANSWER_INPUT_REQUEST);
            if (optionalAnswerInputData.isPresent()) {
                AnswerInputData answerInputData = new Gson().fromJson(optionalAnswerInputData.get().getData(), AnswerInputData.class);
                int nextPageIndex = answerInputData.getCurrentPage() + 1;
                List<AnswerInputItem> answerInputItems = extractItemsFromPages(exerciseData, nextPageIndex);
                saveDataForPages(chatId, answerInputItems, size(exerciseData.getCardPages()), nextPageIndex);
            } else {
                // it's the first time - page = 0
                List<AnswerInputItem> answerInputItems = extractItemsFromPages(exerciseData, 0);
                saveDataForPages(chatId, answerInputItems, size(exerciseData.getCardPages()), 0);
            }
        } else {
            // no pages
            List<AnswerInputItem> answerInputItems = extractItemsFromAllCards(exerciseData);
            saveDataWithoutPages(chatId, answerInputItems);
        }

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.ANSWER_INPUT_REQUEST)
                .build();
    }

    private void saveDataForPages(Long chatId, List<AnswerInputItem> answerInputItems, int totalNumberOfPages, int currentPage) {
        saveData(chatId, answerInputItems, true, totalNumberOfPages, currentPage);
    }

    private void saveDataWithoutPages(Long chatId, List<AnswerInputItem> answerInputItems) {
        saveData(chatId, answerInputItems, false, 0, 0);
    }

    private void saveData(Long chatId, List<AnswerInputItem> answerInputItems, boolean pageable, int totalNumberOfPages, int currentPage) {
        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.ANSWER_INPUT_REQUEST)
                .data(new Gson().toJson(
                        AnswerInputData.builder()
                                .totalNumberOfItems(answerInputItems.size())
                                .currentIndex(0)
                                .answerInputItems(answerInputItems)
                                .pageable(pageable)
                                .totalNumberOfPages(totalNumberOfPages)
                                .currentPage(currentPage)
                                .build()
                ))
                .build());
    }

    private List<AnswerInputItem> extractItemsFromAllCards(ExerciseData exerciseData) {
        return exerciseData.getCards().stream()
                .map(it -> AnswerInputItem.builder()
                        .question(it.getFrontSide())
                        .correctAnswer(it.getBackSide())
                        .build())
                .toList();
    }

    private List<AnswerInputItem> extractItemsFromPages(ExerciseData exerciseData, int pageIndex) {
        return exerciseData.getCardPages().get(pageIndex).stream()
                .map(it -> AnswerInputItem.builder()
                        .question(it.getFrontSide())
                        .correctAnswer(it.getBackSide())
                        .build())
                .toList();
    }
}
