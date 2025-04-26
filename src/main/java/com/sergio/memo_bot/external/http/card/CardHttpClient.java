package com.sergio.memo_bot.external.http.card;

import com.sergio.memo_bot.dto.CardDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(url = "/telegram/card")
public interface CardHttpClient {
    @PostExchange("/add")
    CardDto add(@RequestParam Long cardSetId, @RequestBody CardDto cardDto);
    @PutExchange("/update")
    CardDto update(@RequestBody CardDto cardDto);
    @DeleteExchange("/delete")
    void delete(@RequestParam Long cardId);

}
