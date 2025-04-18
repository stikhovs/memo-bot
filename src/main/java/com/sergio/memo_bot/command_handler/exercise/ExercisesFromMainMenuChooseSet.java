package com.sergio.memo_bot.command_handler.exercise;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.ApiCallService;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExercisesFromMainMenuChooseSet implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXERCISES_FROM_MAIN_MENU_CHOOSE_SET == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        String[] commandAndCardSetId = processableMessage.getText().split("__");
        Long cardSetId = Long.valueOf(commandAndCardSetId[1]);

        CardSetDto chosenCardSet = apiCallService.getCardSet(cardSetId).orElseThrow();

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.EXERCISES_FROM_MAIN_MENU_CHOOSE_SET)
                .data(new Gson().toJson(chosenCardSet))
                .build());

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.EXERCISES_DATA_PREPARE)
                .data(CommandType.EXERCISES_FROM_MAIN_MENU.getCommandText())
                .build());

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                .build();
    }
}