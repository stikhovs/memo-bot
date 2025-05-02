package com.sergio.memo_bot.external.http.card;

import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.external.helper.HttpCallHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardHttpService {

    private final HttpCallHelper httpCallHelper;
    private final CardHttpClient cardHttpClient;

    public List<CardDto> addCards(Long cardSetId, List<CardDto> cards) {
        return httpCallHelper.callOrThrow(
                () -> cards.stream().filter(cardDto -> cardDto.getId() == null)
                        .map(cardDto -> {
                            log.info("Adding card to: {}", cardDto);
                            CardDto result = cardHttpClient.add(cardSetId, cardDto);
                            log.info("Card was added successfully. {}", result);
                            return result;
                        })
                        .collect(Collectors.toList())
                ,
                CardHttpException.class, "Couldn't add cards %s to cardSetId %s".formatted(cards, cardSetId));
    }

    public CardDto updateCard(CardDto cardDto) {
        return httpCallHelper.callOrThrow(
                () -> {
                    log.info("Updating card to: {}", cardDto);
                    CardDto result = cardHttpClient.update(cardDto);
                    log.info("Card was updated successfully. {}", cardDto);
                    return result;
                },
                CardHttpException.class, "Couldn't update card %s".formatted(cardDto));
    }

    public void deleteCard(Long cardId) {
        httpCallHelper.runOrThrow(
                () -> {
                    log.info("Deleting card with id: {}", cardId);
                    cardHttpClient.delete(cardId);
                    log.info("Deleted card with id: {}", cardId);
                },
                CardHttpException.class, "Couldn't delete card with id %s".formatted(cardId));
    }

}
