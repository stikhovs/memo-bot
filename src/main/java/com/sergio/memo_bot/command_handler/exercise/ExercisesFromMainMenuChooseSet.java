package com.sergio.memo_bot.command_handler.exercise;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.card_set.CardSetHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExercisesFromMainMenuChooseSet implements CommandHandler {

    private final CardSetHttpService cardSetHttpService;
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

        CardSetDto chosenCardSet = cardSetHttpService.getCardSet(cardSetId);

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