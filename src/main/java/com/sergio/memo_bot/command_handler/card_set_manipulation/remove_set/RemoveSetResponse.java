package com.sergio.memo_bot.command_handler.card_set_manipulation.remove_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveSetResponse implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.REMOVE_SET_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardSetDto cardSet = chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class);
        apiCallService.deleteCardSet(cardSet.getId());

        chatTempDataService.clear(chatId, CommandType.GET_CARD_SET_INFO);

        return MultipleBotReply.builder()
                .type(BotReplyType.MESSAGE)
                .chatId(chatId)
                .messageId(processableMessage.getMessageId())
                .text("Набор удален")
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.MAIN_MENU)
                .build();
    }
}
