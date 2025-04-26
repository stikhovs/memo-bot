package com.sergio.memo_bot.external.http.card_set;

import com.sergio.memo_bot.dto.CardSetDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.List;

@HttpExchange(url = "/telegram")
public interface CardSetHttpClient {

    @GetExchange("/set-and-cards-by-set-id")
    CardSetDto getCardSet(@RequestParam Long cardSetId);

    @PostExchange("/set/save")
    CardSetDto saveCardSet(@RequestBody CardSetDto cardSetDto);

    @GetExchange("/sets-by-chat")
    List<CardSetDto> getCardSets(@RequestParam Long telegramChatId);

    @GetExchange("/sets-by-category")
    List<CardSetDto> getCardSetsByCategory(@RequestParam Long categoryId);

    @PutExchange("/set/update")
    CardSetDto updateCardSet(@RequestBody CardSetDto cardSetDto);

    @PutExchange("/set/update-category-batch")
    void updateCategoryBatch(@RequestParam Long categoryId, @RequestBody List<Long> cardSetIds);

    @DeleteExchange("/set/delete")
    void deleteCardSet(@RequestParam Long cardSetId);

}
