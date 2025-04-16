package com.sergio.memo_bot.command_handler.category;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryMenuDataRequest implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CATEGORY_MENU_DATA == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        List<CategoryDto> categories = apiCallService.getCategoriesByChatId(chatId);

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.CATEGORY_MENU_DATA)
                .data(new Gson().toJson(categories))
                .build());

        /*if (isEmpty(categories)) {
            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text("Вы пока не создали категорий. Хотите создать сейчас?")
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                            Pair.of("Да", CommandType.CREATE_CATEGORY_REQUEST),
                            Pair.of("Нет", CommandType.MAIN_MENU)
                    )))
                    .build();
        }*/

        return BotPartReply.builder()
                .chatId(chatId)
                .nextCommand(CommandType.CATEGORY_MENU)
                .previousProcessableMessage(processableMessage)
                .build();
    }
}