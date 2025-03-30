package com.sergio.memo_bot.command_handler.create_set;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveSetRequest implements CommandHandler {
    public static final String CREATE_SET_URL = "/set/save";

    private final RestTemplate restTemplate;
    private final ChatTempDataRepository chatTempDataRepository;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SAVE_CARD_SET_REQUEST == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        List<ChatTempData> tempDataList = chatTempDataRepository.findByChatId(processableMessage.getChatId());
        if (CollectionUtils.isEmpty(tempDataList)) {
            throw new RuntimeException("Temp data should not be empty on this step");
        }

        CardSetDto cardSetDto = new Gson().fromJson(tempDataList.getFirst().getData(), CardSetDto.class);

        log.info("Сохраняем: {}", cardSetDto);
        ResponseEntity<CardSetDto> response = callCreateSetApi(cardSetDto.toBuilder().telegramChatId(processableMessage.getChatId()).build());

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Сохранено: {}", response.getBody());
            chatTempDataRepository.deleteByChatId(processableMessage.getChatId());

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
        return restTemplate.postForEntity(
                CREATE_SET_URL,
                cardSetDto,
                CardSetDto.class);
//        return ResponseEntity.ok(cardSetDto);
    }
}
