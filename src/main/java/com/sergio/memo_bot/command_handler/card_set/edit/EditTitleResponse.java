package com.sergio.memo_bot.command_handler.card_set.edit;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatMessageService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.external.ApiCallService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.NEW_CARD_SET_TITLE_SUCCESSFULLY_SAVED;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditTitleResponse implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;
    private final ChatMessageService chatMessageService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_TITLE_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class);

        CardSetDto newCardSet = cardSetDto.toBuilder().title(processableMessage.getText()).build();
        CardSetDto savedCardSet = apiCallService.updateCardSet(newCardSet);

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .data(new Gson().toJson(savedCardSet))
                .command(CommandType.GET_CARD_SET_INFO)
                .build());

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(NEW_CARD_SET_TITLE_SUCCESSFULLY_SAVED)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.GET_CARD_SET_INFO)
                        .previousProcessableMessage(processableMessage.toBuilder().text(CommandType.GET_CARD_SET_INFO.getCommandText()).build())
                        .build())
                .build();
    }
}
