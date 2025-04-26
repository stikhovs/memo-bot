package com.sergio.memo_bot.external.http.user;

import com.sergio.memo_bot.dto.UserDto;
import com.sergio.memo_bot.external.helper.HttpCallHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserHttpService {

    private final HttpCallHelper httpCallHelper;
    private final UserHttpClient userHttpClient;

    public UserDto getUser(Long telegramUserId) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Getting user by chatId {}", telegramUserId);
                    UserDto user = userHttpClient.getUser(telegramUserId);
                    log.info("Got user {}", user);
                    return user;
                },
                UserHttpException.class, "Couldn't get user by chatId %s".formatted(telegramUserId));
    }

    public UserDto createUser(UserDto userDto) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Creating user {}", userDto);
                    UserDto user = userHttpClient.createUser(userDto);
                    log.info("Created user {}", user);
                    return user;
                },
                UserHttpException.class, "Couldn't create user %s".formatted(userDto));
    }
}
