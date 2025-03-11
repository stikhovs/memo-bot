package com.sergio.memo_bot.path.start;

import com.sergio.memo_bot.dto.UserDto;
import com.sergio.memo_bot.update_handler.text.path.TextPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Start implements TextPath {
    private static final String START = "/start";
    public static final String CREATE_USER_URL = "/telegram/user/create";
    public static final String GET_USER_URL = "/telegram/user?telegramUserId=%s";

    private final RestTemplate restTemplate;

    @Override
    public boolean canProcess(Message message) {
        return StringUtils.equalsIgnoreCase(START, message.getText());
    }

    @Override
    public BotApiMethodMessage process(Message message, Long chatId) {
        User user = message.getFrom();
        return getOrCreateUser(user, chatId);
    }

    private SendMessage getOrCreateUser(User user, Long chatId) {
        ResponseEntity<UserDto> response = callGetUserApi(user);
        if (response.getStatusCode().is2xxSuccessful()) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format("Добро пожаловать назад, %s!", user.getUserName()))
                    .replyMarkup(
                            getInlineKeyboardMarkup()
                    )
                    .build();
        }
        return registerUser(user, chatId);
    }

    public SendMessage registerUser(User user, Long chatId) {

        ResponseEntity<UserDto> response = callCreateUserApi(user, chatId);
        if (response.getStatusCode().is2xxSuccessful()) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(String.format("Вы зарегистрировались, %s!", user.getUserName()))
                    .replyMarkup(
                            getInlineKeyboardMarkup()
                    )
                    .build();
        } else {
            log.error("Status code: {}", response.getStatusCode().value());
            throw new RuntimeException("Couldn't register the user");
        }
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder().text("Создать набор").callbackData("Create set").build(),
                        InlineKeyboardButton.builder().text("Импортировать готовый\n набор").callbackData("Import set").build()
                ))
                .build();
    }

    private ResponseEntity<UserDto> callCreateUserApi(User user, Long chatId) {
        return restTemplate.postForEntity(
                CREATE_USER_URL,
                UserDto.builder()
                        .username(user.getUserName())
                        .telegramUserId(user.getId())
                        .telegramChatId(chatId)
                        .build(),
                UserDto.class
        );
    }

    private ResponseEntity<UserDto> callGetUserApi(User user) {
        try {
            return restTemplate.exchange(
                    GET_USER_URL.formatted(user.getId()),
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
