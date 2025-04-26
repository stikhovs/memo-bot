package com.sergio.memo_bot.external.http.category;

import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.external.helper.HttpCallHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryHttpService {

    private final HttpCallHelper httpCallHelper;
    private final CategoryHttpClient categoryHttpClient;

    public CategoryDto getById(Long categoryId) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Getting category by id {}", categoryId);
                    CategoryDto category = categoryHttpClient.getById(categoryId);
                    log.info("Got category: {}", category);
                    return category;
                },
                CategoryHttpException.class, "Couldn't get category by id %s".formatted(categoryId));
    }

    public List<CategoryDto> getByChatId(Long chatId) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Getting categories by chatId {}", chatId);
                    List<CategoryDto> categories = categoryHttpClient.getByChatId(chatId);
                    log.info("Got categories: {}", categories);
                    return categories;
                },
                CategoryHttpException.class, "Couldn't get categories by chatId %s".formatted(chatId));
    }

    public CategoryDto save(Long chatId, CategoryDto categoryDto) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Saving category {} for chatId {}", categoryDto, chatId);
                    CategoryDto category = categoryHttpClient.save(chatId, categoryDto);
                    log.info("Saved category {} for chatId {}", category, chatId);
                    return category;
                },
                CategoryHttpException.class, "Couldn't save category %s for chatId %s".formatted(categoryDto, chatId));
    }

    public CategoryDto update(CategoryDto categoryDto) {
        return httpCallHelper.callOrThrow(() -> {
                    log.info("Updating category: {}", categoryDto);
                    CategoryDto category = categoryHttpClient.update(categoryDto);
                    log.info("Updated category: {}", category);
                    return category;
                },
                CategoryHttpException.class, "Couldn't update category %s".formatted(categoryDto));
    }

    public void delete(Long categoryId, boolean keepSets) {
        httpCallHelper.runOrThrow(() -> {
                    log.info("Deleting category with id {}, keepSets: {}", categoryId, keepSets);
                    categoryHttpClient.delete(categoryId, keepSets);
                    log.info("Deleted category with id {}, keepSets: {}", categoryId, keepSets);
                },
                CategoryHttpException.class, "Couldn't delete category with id %s".formatted(categoryId));
    }
}
