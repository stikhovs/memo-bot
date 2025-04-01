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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sergio.memo_bot.util.UrlConstant.GET_ALL_SETS_URL;

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
        ResponseEntity<List<CardSetDto>> response = callApi(processableMessage);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Response: {}", response.getBody());
            chatTempDataService.clearAndSave(processableMessage.getChatId(),
                    ChatTempData.builder()
                            .chatId(processableMessage.getChatId())
                            .data(new Gson().toJson(response.getBody()))
                            .command(CommandType.GET_ALL_SETS)
                            .build());

            return BotReply.builder()
                    .type(BotReplyType.MESSAGE)
                    .chatId(processableMessage.getChatId())
                    .messageId(processableMessage.getMessageId())
                    .text("Выберите набор")
                    .replyMarkup(MarkUpUtil.getInlineCardSetButtons(response.getBody()))
                    .build();
        }

        return MultipleBotReply.builder()
                .type(BotReplyType.MESSAGE)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .text("Что-то пошло не так. Попробуйте снова")
                .nextCommand(CommandType.MAIN_MENU)
                .build();

    }

    private ResponseEntity<List<CardSetDto>> callApi(ProcessableMessage processableMessage) {
        return apiCallService.getList(GET_ALL_SETS_URL.formatted(processableMessage.getUserId()), CardSetDto.class);
    }

}
