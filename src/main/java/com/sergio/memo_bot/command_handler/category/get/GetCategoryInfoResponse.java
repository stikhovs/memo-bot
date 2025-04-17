package com.sergio.memo_bot.command_handler.category.get;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCategoryInfoResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_CATEGORY_INFO_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CategoryDto categoryDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE, CategoryDto.class);
        List<CardSetDto> cardSets = chatTempDataService.mapDataToList(chatId, CommandType.GET_CATEGORY_CARD_SET_INFO, CardSetDto.class);

        boolean isDefaultCategory = categoryDto.getTitle().equals("default");

        return BotMessageReply.builder()
                .chatId(chatId)
                .text("Категория \"%s\". ".formatted(categoryDto.getTitle()) +
                        "\n\nКоличество наборов: %s. ".formatted(size(cardSets)) +
                        (isNotEmpty(cardSets)
                                ? "\n\nНаборы: \n" + cardSets.stream().map(CardSetDto::getTitle).collect(Collectors.joining(";\n"))
                                : ""
                        ))
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                                Pair.of("Выбрать наборы", CommandType.CHOOSE_CARD_SET_IN_CATEGORY),
                                Pair.of("Создать набор в этой категории", CommandType.CREATE_SET_FOR_CHOSEN_CATEGORY),
                                Pair.of("Перенести наборы в эту категорию", CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE),
                                isDefaultCategory ? Pair.of(null, null) : Pair.of("Редактировать категорию", CommandType.EDIT_CATEGORY_REQUEST),
                                Pair.of("Упражнения с категорией", CommandType.EXERCISES_FROM_CATEGORY),
                                Pair.of("Назад", CommandType.GET_ALL_CATEGORIES_RESPONSE)
                        )))
                .build();
    }
}