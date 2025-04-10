package com.sergio.memo_bot.util;

import com.google.gson.reflect.TypeToken;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static com.sergio.memo_bot.util.UrlConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiCallService {

    private final RestTemplate restTemplate;
    private final RequestBuilder requestBuilder;

    public UserDto saveUser(UserDto userDto) {
        log.info("Registering user: {}", userDto);
        ResponseEntity<UserDto> response = post(CREATE_USER_URL, userDto, UserDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            UserDto savedUser = response.getBody();
            log.info("User successfully registered: {}", savedUser);
            return savedUser;
        }
        log.error("Couldn't register the user: {}. Status code: {}", userDto, response.getStatusCode().value());
        throw new RuntimeException("Couldn't register the user");
    }

    public Optional<UserDto> getUser(Long chatId) {
        log.info("Getting user with chatId: {}", chatId);
        ResponseEntity<UserDto> response = get(GET_USER_URL.formatted(chatId), UserDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return Optional.of(response.getBody());
        }
        log.info("Couldn't find the user with chatId: {}. Response code: {}", chatId, response.getStatusCode().value());
        return Optional.empty();
    }

    public CardSetDto saveCardSet(CardSetDto cardSetDto) {
        log.info("Saving cardSet: {}", cardSetDto);
        ResponseEntity<CardSetDto> response = post(UrlConstant.CREATE_SET_URL, cardSetDto, CardSetDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            CardSetDto savedCardSet = response.getBody();
            log.info("CardSet successfully saved: {}", savedCardSet);
            return savedCardSet;
        }
        log.error("Couldn't save the cardSet: {} . Status code: {}", cardSetDto, response.getStatusCode().value());
        throw new RuntimeException("Couldn't save the cardSet");
    }

    public Optional<CardSetDto> getCardSet(Long cardSetId) {
        log.info("Getting cardSet with cardSetId: {}", cardSetId);
        ResponseEntity<CardSetDto> response = get(GET_SET_AND_CARDS_URL.formatted(cardSetId), CardSetDto.class);
        Optional<CardSetDto> result = Optional.empty();
        if (response.getStatusCode().is2xxSuccessful()) {
            result = Optional.of(response.getBody());
            log.info("Got cardSet: {}", result.get());
            return result;
        }
        log.info("Couldn't find the cardSet with cardSetId: {}. Response code: {}", cardSetId, response.getStatusCode().value());
        return result;
    }

    public List<CardSetDto> getCardSets(Long chatId) {
        log.info("Getting all cardSets for chatId: {}", chatId);
        ResponseEntity<List<CardSetDto>> response = getList(GET_ALL_SETS_URL.formatted(chatId), CardSetDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        log.info("Couldn't find cardSets for chatId: {}. Response code: {}", chatId, response.getStatusCode().value());
        return List.of();
    }

    public CardSetDto updateCardSet(CardSetDto cardSetDto) {
        log.info("Updating cardSet to: {}", cardSetDto);
        ResponseEntity<CardSetDto> response = put(UPDATE_SET_URL, cardSetDto, CardSetDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("CardSet was updated successfully. {}", cardSetDto);
            return response.getBody();
        }
        log.error("Couldn't update cardSet to: {}. Response code: {}", cardSetDto, response.getStatusCode().value());
        throw new RuntimeException("Couldn't update the cardSet");
    }

    public CardDto updateCard(CardDto cardDto) {
        log.info("Updating card to: {}", cardDto);
        ResponseEntity<CardDto> response = put(UPDATE_CARD_URL, cardDto, CardDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Card was updated successfully. {}", cardDto);
            return response.getBody();
        }
        log.error("Couldn't update card to: {}. Response code: {}", cardDto, response.getStatusCode().value());
        throw new RuntimeException("Couldn't update the card");
    }

    public CardDto addCard(Long cardSetId, CardDto cardDto) {
        log.info("Adding card to: {}", cardDto);
        ResponseEntity<CardDto> response = post(ADD_CARD_URL.formatted(cardSetId), cardDto, CardDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Card was added successfully. {}", cardDto);
            return response.getBody();
        }
        log.error("Couldn't add card to: {}. Response code: {}", cardDto, response.getStatusCode().value());
        throw new RuntimeException("Couldn't add the card");
    }

    public void deleteCardSet(Long cardSetId) {
        log.info("Deleting cardSet with id: {}", cardSetId);
        delete(DELETE_CARD_SET_URL.formatted(cardSetId));
        log.info("Deleted cardSet with id: {}", cardSetId);
    }

    public void deleteCard(Long cardId) {
        log.info("Deleting card with id: {}", cardId);
        delete(DELETE_CARD_URL.formatted(cardId));
        log.info("Deleted card with id: {}", cardId);
    }

    private <T> ResponseEntity<T> get(String url, Class<T> resultType) {
        try {
            return restTemplate.exchange(
                    requestBuilder.build(HttpMethod.GET, restTemplate.getUriTemplateHandler().expand(url)),
                    ParameterizedTypeReference.forType(TypeToken.get(resultType).getType())
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    private <T> ResponseEntity<List<T>> getList(String url, Class<T> elementType) {
        try {
            return restTemplate.exchange(
                    requestBuilder.build(HttpMethod.GET, restTemplate.getUriTemplateHandler().expand(url)),
                    ParameterizedTypeReference.forType(TypeToken.getParameterized(List.class, elementType).getType())
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    private <T, R> ResponseEntity<R> post(String url, T requestBody, Class<R> responseType) {
        return restTemplate.exchange(
                requestBuilder.build(HttpMethod.POST, restTemplate.getUriTemplateHandler().expand(url), requestBody),
                responseType
        );
    }

    private <T, R> ResponseEntity<R> put(String url, T requestBody, Class<R> responseType) {
        try {
            return restTemplate.exchange(
                    requestBuilder.build(HttpMethod.PUT, restTemplate.getUriTemplateHandler().expand(url), requestBody),
                    responseType
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.badRequest().build();
            }
            throw e;
        }
    }

    private  void delete(String url) {
        requestBuilder.build(HttpMethod.DELETE, restTemplate.getUriTemplateHandler().expand(url));
    }

}
