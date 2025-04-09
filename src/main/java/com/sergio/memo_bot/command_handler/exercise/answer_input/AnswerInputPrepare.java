package com.sergio.memo_bot.command_handler.exercise.answer_input;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputData;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputItem;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);

        List<AnswerInputItem> answerInputItems = cardSetDto.getCards().stream()
                .map(it -> AnswerInputItem.builder()
                        .question(it.getFrontSide())
                        .correctAnswer(it.getBackSide())
                        .build())
                .toList();

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                        .chatId(processableMessage.getChatId())
                        .command(CommandType.ANSWER_INPUT_REQUEST)
                        .data(new Gson().toJson(
                                AnswerInputData.builder()
                                        .totalNumberOfItems(answerInputItems.size())
                                        .currentIndex(0)
                                        .answerInputItems(answerInputItems)
                                        .build()
                        ))
                .build());

        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.ANSWER_INPUT_REQUEST)
                .build();
    }
}
