package com.sergio.memo_bot.command_handler.exercise;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExercisesFromCardSet implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXERCISES_FROM_CARD_SET == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.EXERCISES_DATA_PREPARE)
                        .data(CommandType.EXERCISES_FROM_CARD_SET.getCommandText())
                .build());
        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                .build();
    }
}