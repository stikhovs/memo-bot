package com.sergio.memo_bot.command_handler.category.add_sets_to_category;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.category.add_sets_to_category.dto.AddSetToCategoryData;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.external.ApiCallService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.notEqual;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseSetsForCategoryPrepare implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CategoryDto chosenCategory = chatTempDataService.mapDataToType(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE, CategoryDto.class);

        List<CardSetDto> cardSets = apiCallService.getCardSets(chatId)
                .stream()
                .filter(cardSetDto -> notEqual(cardSetDto.getCategoryId(), chosenCategory.getId()))
                .toList();

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE)
                .data(new Gson().toJson(
                        AddSetToCategoryData.builder()
                                .category(chosenCategory)
                                .allCardSets(cardSets)
                                .chosenCardSets(List.of())
                                .build()))
                .build());

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.CHOOSE_SETS_FOR_CATEGORY_REQUEST)
                .build();
    }

}