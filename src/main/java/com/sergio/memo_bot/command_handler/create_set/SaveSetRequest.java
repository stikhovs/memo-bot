package com.sergio.memo_bot.command_handler.create_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveSetRequest implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SAVE_CARD_SET_REQUEST == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.ADD_CARD_RESPONSE, CardSetDto.class);

        log.info("Сохраняем: {}", cardSetDto);
        ResponseEntity<CardSetDto> response = callCreateSetApi(cardSetDto.toBuilder().telegramChatId(processableMessage.getChatId()).build());

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Сохранено: {}", response.getBody());
            chatTempDataService.clear(processableMessage.getChatId());

            return BotPartReply.builder()
                    .nextCommand(CommandType.SAVE_CARD_SET_RESPONSE)
                    .previousProcessableMessage(processableMessage)
                    .messageId(processableMessage.getMessageId())
                    .chatId(processableMessage.getChatId())
//                    .type(BotReplyType.MESSAGE)
//                    .text()
                    .build();
        }
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Что-то пошло не так")
                .chatId(processableMessage.getChatId())
                .build();
    }

    private ResponseEntity<CardSetDto> callCreateSetApi(CardSetDto cardSetDto) {
        return apiCallService.post(UrlConstant.CREATE_SET_URL, cardSetDto, CardSetDto.class);
//        return ResponseEntity.ok(cardSetDto);
    }
}
