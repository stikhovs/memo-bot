package com.sergio.memo_bot.command_handler.card_set_manipulation;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllSetsRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ApiCallService apiCallService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_ALL_SETS == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        List<CardSetDto> cardSets = apiCallService.getCardSets(processableMessage.getChatId());

        if (CollectionUtils.isNotEmpty(cardSets)) {
            chatTempDataService.clearAndSave(processableMessage.getChatId(),
                    ChatTempData.builder()
                            .chatId(processableMessage.getChatId())
                            .data(new Gson().toJson(cardSets))
                            .command(CommandType.GET_ALL_SETS)
                            .build());

            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
//                    .type(BotReplyType.EDIT_MESSAGE_TEXT)
//                    .messageId(processableMessage.getMessageId())
                    .text("Выберите набор")
                    .replyMarkup(MarkUpUtil.getInlineCardSetButtons(cardSets))
                    .build();
        }

        return BotMessageReply.builder()
//                .type(BotReplyType.MESSAGE)
                .chatId(processableMessage.getChatId())
//                .messageId(processableMessage.getMessageId())
                .text("Что-то пошло не так. Попробуйте снова")
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.MAIN_MENU)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();

    }

}
