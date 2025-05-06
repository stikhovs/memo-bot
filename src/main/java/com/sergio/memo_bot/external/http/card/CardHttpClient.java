package com.sergio.memo_bot.external.http.card;

import com.sergio.memo_bot.dto.CardDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;

@HttpExchange(url = "/telegram/card")
public interface CardHttpClient {
    @PostExchange("/add")
    CardDto add(@RequestParam Long cardSetId, @RequestBody CardDto cardDto);
    @PostExchange("/add-batch")
    List<CardDto> addBatch(@RequestParam Long cardSetId, @RequestBody List<CardDto> cards);
    @PutExchange("/update")
    CardDto update(@RequestBody CardDto cardDto);
    @DeleteExchange("/delete")
    void delete(@RequestParam Long cardId);

}
