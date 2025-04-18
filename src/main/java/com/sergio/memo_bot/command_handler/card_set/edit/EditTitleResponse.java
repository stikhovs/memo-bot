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
import com.sergio.memo_bot.util.ApiCallService;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.NextReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);


        CardSetDto newCardSet = cardSetDto.toBuilder().title(processableMessage.getText()).build();
        CardSetDto savedCardSet = apiCallService.updateCardSet(newCardSet);
        Integer messageId = chatMessageService.findAllBotMessages(processableMessage.getChatId())
                .stream().filter(ChatMessage::isHasButtons)
                .findFirst()
                .map(ChatMessage::getMessageId)
                .orElse(null);

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .data(new Gson().toJson(savedCardSet))
                .command(CommandType.GET_CARD_SET_INFO)
                .build());

        /*return BotReply.builder()
                .type(BotReplyType.EDIT_MESSAGE_REPLY_MARKUP)
                .chatId(processableMessage.getChatId())
                .messageId(messageId)
                .nextReply(
                        MultipleBotReply.builder()
                                .type(BotReplyType.MESSAGE)
                                .text("Новое название успешно сохранено")
                                .messageId(processableMessage.getMessageId())
                                .chatId(processableMessage.getChatId())
                                .previousProcessableMessage(processableMessage.toBuilder().text(CommandType.GET_CARD_SET_INFO.getCommandText()).build())
                                .nextCommand(CommandType.GET_CARD_SET_INFO)
                                .build()
                )
                .build();*/
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
//                .type(BotReplyType.MESSAGE)
                .text("Новое название успешно сохранено")
//                .messageId(processableMessage.getMessageId())
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.GET_CARD_SET_INFO)
                        .previousProcessableMessage(processableMessage.toBuilder().text(CommandType.GET_CARD_SET_INFO.getCommandText()).build())
                        .build())
                .build();
    }
}
