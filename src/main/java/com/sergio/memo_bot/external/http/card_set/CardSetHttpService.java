package com.sergio.memo_bot.external.http.card_set;

import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.external.helper.HttpCallHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardSetHttpService {

    private final HttpCallHelper httpCallHelper;
    private final CardSetHttpClient cardSetHttpClient;

    public CardSetDto getCardSet(Long cardSetId) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Getting cardSet with cardSetId: {}", cardSetId);
                    CardSetDto cardSet = cardSetHttpClient.getCardSet(cardSetId);
                    log.info("Got cardSet: {}", cardSet);
                    return cardSet;
                },
                CardSetHttpException.class, "Couldn't get cardSet with id %s".formatted(cardSetId));
    }

    public CardSetDto saveCardSet(CardSetDto cardSetDto) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Saving cardSet: {}", cardSetDto);
                    CardSetDto savedCardSet = cardSetHttpClient.saveCardSet(cardSetDto);
                    log.info("CardSet successfully saved: {}", savedCardSet);
                    return savedCardSet;
                },
                CardSetHttpException.class, "Couldn't save cardSet %s".formatted(cardSetDto));
    }

    public List<CardSetDto> getCardSets(Long telegramChatId) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Getting all cardSets for chatId: {}", telegramChatId);
                    List<CardSetDto> cardSets = cardSetHttpClient.getCardSets(telegramChatId);
                    log.info("Got cardSets successfully for chatId: {}", cardSets);
                    return cardSets;
                },
                CardSetHttpException.class, "Couldn't find cardSets for chatId %s".formatted(telegramChatId));
    }

    public List<CardSetDto> getCardSetsByCategory(Long categoryId) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Getting all cardSets for categoryId: {}", categoryId);
                    List<CardSetDto> cardSets = cardSetHttpClient.getCardSetsByCategory(categoryId);
                    log.info("Got cardSets successfully for categoryId: {}", cardSets);
                    return cardSets;
                },
                CardSetHttpException.class, "Couldn't find cardSets for categoryId: %s".formatted(categoryId));
    }

    public CardSetDto updateCardSet(CardSetDto cardSetDto) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Updating cardSet: {}", cardSetDto);
                    CardSetDto updatedCardSet = cardSetHttpClient.updateCardSet(cardSetDto);
                    log.info("CardSet successfully updated: {}", updatedCardSet);
                    return updatedCardSet;
                },
                CardSetHttpException.class, "Couldn't update cardSet %s".formatted(cardSetDto));
    }

    public void updateCategoryBatch(Long categoryId, List<Long> cardSetIds) {
        httpCallHelper.runOrThrow(() -> {
                    log.info("Updating categoryId to {} for cardSetIds {}", categoryId, cardSetIds);
                    cardSetHttpClient.updateCategoryBatch(categoryId, cardSetIds);
                    log.info("CategoryId successfully updated to {} for cardSetIds {}", categoryId, cardSetIds);
                },
                CardSetHttpException.class, "Couldn't update categoryId to %s for cardSetIds %s".formatted(categoryId, cardSetIds));
    }

    public void deleteCardSet(Long cardSetId) {
        httpCallHelper.runOrThrow(() -> {
                    log.info("Deleting cardSet with id: {}", cardSetId);
                    cardSetHttpClient.deleteCardSet(cardSetId);
                    log.info("Deleted cardSet with id: {}", cardSetId);
                },
                CardSetHttpException.class, "Couldn't delete cardSet with id %s".formatted(cardSetId));
    }
}
