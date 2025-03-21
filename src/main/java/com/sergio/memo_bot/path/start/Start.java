package com.sergio.memo_bot.path.start;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.dto.UserDto;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.BaseProcessor;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.MarkUpUtil;
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
public class Start extends BaseProcessor {
    public static final String CREATE_USER_URL = "/telegram/user/create";
    public static final String GET_USER_URL = "/telegram/user?telegramUserId=%s";

    private final RestTemplate restTemplate;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return userStateType == UserStateType.START;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        return getOrCreateUser(processableMessage);
    }

    /*@Override
    public boolean canProcess(Message message) {
        return StringUtils.equalsIgnoreCase(START, message.getText());
    }

    @Override
    public BotApiMethodMessage process(Message message, Long chatId) {
        User user = message.getFrom();
        return getOrCreateUser(user, chatId);
    }
*/
    private BotReply getOrCreateUser(ProcessableMessage processableMessage) {
        ResponseEntity<UserDto> response = callGetUserApi(processableMessage);
        if (response.getStatusCode().is2xxSuccessful()) {
            return BotReply.builder()
                    .type(BotReplyType.MESSAGE)
                    .chatId(processableMessage.getChatId())
                    .text(String.format("Добро пожаловать назад, %s!", processableMessage.getUsername()))
                    .replyMarkup(
                            getInlineKeyboardMarkup()
                    )
                    .build();
        }
        return registerUser(processableMessage);
    }

    public BotReply registerUser(ProcessableMessage processableMessage) {

        ResponseEntity<UserDto> response = callCreateUserApi(processableMessage);
        if (response.getStatusCode().is2xxSuccessful()) {
            return BotReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text(String.format("Вы зарегистрировались, %s!", processableMessage.getUsername()))
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
        return MarkUpUtil.getInlineKeyboardMarkup(List.of(
                Pair.of("Создать набор", CommandType.CREATE_SET),
                Pair.of("Импортировать набор", CommandType.IMPORT_SET)
        ));
    }

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
