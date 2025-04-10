package com.sergio.memo_bot.util;

import com.sergio.memo_bot.configuration.BotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class RequestBuilder {

    private final BotProperties botProperties;

    public <T> RequestEntity<T> build(HttpMethod method, URI uri, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Auth", botProperties.getBotSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new RequestEntity<>(body, headers, method, uri);
    }

    public RequestEntity<Void> build(HttpMethod method, URI uri) {
        return build(method, uri, null);
    }

}
