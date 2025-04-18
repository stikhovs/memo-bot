package com.sergio.memo_bot.command_handler.card_set.edit.move_to_category;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseCategoryForSetMoving implements CommandHandler {
    private final ChatTempDataService chatTempDataService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CHOOSE_CATEGORY_FOR_SET_MOVING == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        String[] commandAndCategoryId = processableMessage.getText().split("__");
        Long categoryId = Long.valueOf(commandAndCategoryId[1]);

        CategoryDto chosenCategory = chatTempDataService.mapDataToList(chatId, CommandType.MOVE_SET_TO_ANOTHER_CATEGORY, CategoryDto.class)
                .stream()
                .filter(categoryDto -> categoryDto.getId().equals(categoryId))
                .findFirst()
                .orElseThrow();

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.CHOOSE_CATEGORY_FOR_SET_MOVING)
                .data(new Gson().toJson(chosenCategory))
                .build());

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.SAVE_NEW_CATEGORY_FOR_SETS)
                .build();
    }
}