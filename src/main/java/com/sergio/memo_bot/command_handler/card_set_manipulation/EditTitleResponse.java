package com.sergio.memo_bot.command_handler.card_set_manipulation;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditTitleResponse implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_TITLE_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);


        CardSetDto newCardSet = cardSetDto.toBuilder().title(processableMessage.getText()).build();
        CardSetDto savedCardSet = apiCallService.updateCardSet(newCardSet);

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .data(new Gson().toJson(savedCardSet))
                .command(CommandType.GET_CARD_SET_INFO)
                .build());

        return MultipleBotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Новое название успешно сохранено")
                .messageId(processableMessage.getMessageId())
                .chatId(processableMessage.getChatId())
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.MAIN_MENU)
                .build();
    }
}
