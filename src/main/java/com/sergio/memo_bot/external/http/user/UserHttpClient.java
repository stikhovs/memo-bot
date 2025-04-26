package com.sergio.memo_bot.external.http.user;

import com.sergio.memo_bot.dto.UserDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/telegram/user")
public interface UserHttpClient {

    @GetExchange
    UserDto getUser(@RequestParam Long telegramUserId);

    @PostExchange("/create")
    UserDto createUser(@RequestBody UserDto userDto);

}
