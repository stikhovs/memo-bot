package com.sergio.memo_bot.command_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.dto.UserDto;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Start implements CommandHandler {
    public static final String CREATE_USER_URL = "/telegram/user/create";
    public static final String GET_USER_URL = "/telegram/user?telegramUserId=%s";

    private final RestTemplate restTemplate;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.START == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        return getOrCreateUser(processableMessage);
    }

    private Reply getOrCreateUser(ProcessableMessage processableMessage) {
        ResponseEntity<UserDto> response = callGetUserApi(processableMessage);
        if (response.getStatusCode().is2xxSuccessful()) {
            return BotPartReply.builder()
                    .type(BotReplyType.MESSAGE)
                    .previousProcessableMessage(processableMessage)
                    .chatId(processableMessage.getChatId())
                    .messageId(processableMessage.getMessageId())
                    .nextCommand(CommandType.MAIN_MENU)
                    .build();
        }
        return registerUser(processableMessage);
    }

    public Reply registerUser(ProcessableMessage processableMessage) {

        ResponseEntity<UserDto> response = callCreateUserApi(processableMessage);
        if (response.getStatusCode().is2xxSuccessful()) {
            return MultipleBotReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text("Добро пожаловать!")
                    .nextCommand(CommandType.MAIN_MENU)
                    .chatId(processableMessage.getChatId())
                    .messageId(processableMessage.getMessageId())
                    .build();
        } else {
            log.error("Status code: {}", response.getStatusCode().value());
            throw new RuntimeException("Couldn't register the user");
        }
    }

    /*private InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return MarkUpUtil.getInlineKeyboardMarkup(List.of(
                Pair.of("Создать набор", CommandType.CREATE_SET),
                Pair.of("Импортировать набор", CommandType.IMPORT_SET)
        ));
    }*/

    private ResponseEntity<UserDto> callCreateUserApi(ProcessableMessage processableMessage) {
        return restTemplate.postForEntity(
                CREATE_USER_URL,
                UserDto.builder()
                        .username(processableMessage.getUsername())
                        .telegramUserId(processableMessage.getUserId())
                        .telegramChatId(processableMessage.getChatId())
                        .build(),
                UserDto.class
        );
    }

    private ResponseEntity<UserDto> callGetUserApi(ProcessableMessage processableMessage) {
        try {
            return restTemplate.exchange(
                    GET_USER_URL.formatted(processableMessage.getUserId()),
                    HttpMethod.GET,
                    RequestEntity.EMPTY,
                    UserDto.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }
}
