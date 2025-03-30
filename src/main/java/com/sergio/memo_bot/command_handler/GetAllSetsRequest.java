package com.sergio.memo_bot.command_handler;

import com.google.gson.Gson;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllSetsRequest implements CommandHandler {

    public static final String GET_ALL_SETS_URL = "/telegram/sets-by-chat?telegramChatId=%s";

    private final RestTemplate restTemplate;
    private final ChatTempDataRepository chatTempDataRepository;

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
            chatTempDataRepository.deleteByChatId(processableMessage.getChatId());
            chatTempDataRepository.save(ChatTempData.builder()
                    .chatId(processableMessage.getChatId())
                    .data(new Gson().toJson(response.getBody()))
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
        try {
            return restTemplate.exchange(
                    GET_ALL_SETS_URL.formatted(processableMessage.getUserId()),
                    HttpMethod.GET,
                    RequestEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

}
