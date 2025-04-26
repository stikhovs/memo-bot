package com.sergio.memo_bot.external.http.category;

import com.sergio.memo_bot.dto.CategoryDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.List;

@HttpExchange(url = "/telegram/category")
public interface CategoryHttpClient {

    @GetExchange
    CategoryDto getById(@RequestParam Long categoryId);

    @GetExchange("/by-chat")
    List<CategoryDto> getByChatId(@RequestParam Long chatId);

    @PostExchange("/save")
    CategoryDto save(@RequestParam Long chatId, @RequestBody CategoryDto categoryDto);

    @PutExchange("/update")
    CategoryDto update(@RequestBody CategoryDto categoryDto);

    @DeleteExchange("/delete")
    void delete(@RequestParam Long categoryId, @RequestParam boolean keepSets);

}
