package com.sergio.memo_bot.util;

import com.sergio.memo_bot.dto.CardSetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ApiCallService {

    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> get(String url) {
        try {
            return restTemplate.exchange(
                    url,
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

    public <T, R> ResponseEntity<R> post(String url, T requestBody, Class<R> responseType ) {
        return restTemplate.postForEntity(
                url,
                requestBody,
                responseType
        );
    }
}
