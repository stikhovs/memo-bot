package com.sergio.memo_bot.util;

import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiCallService {

    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> get(String url, Class<T> resultType) {
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    RequestEntity.EMPTY,
                    ParameterizedTypeReference.forType(TypeToken.get(resultType).getType())
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    public <T> ResponseEntity<List<T>> getList(String url, Class<T> elementType) {
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    RequestEntity.EMPTY,
                    ParameterizedTypeReference.forType(TypeToken.getParameterized(List.class, elementType).getType())
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
