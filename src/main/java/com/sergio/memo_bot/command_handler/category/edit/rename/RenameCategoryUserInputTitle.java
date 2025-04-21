package com.sergio.memo_bot.command_handler.category.edit.rename;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.external.ApiCallService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RenameCategoryUserInputTitle implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;
    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.RENAME_CATEGORY_USER_INPUT_TITLE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        chatAwaitsInputService.clear(chatId);

        String categoryTitle = processableMessage.getText();
        CategoryDto categoryDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE, CategoryDto.class);


        CategoryDto updatedCategory = apiCallService.updateCategory(categoryDto.toBuilder()
                .title(categoryTitle)
                .build());

        log.info("Updated category: {}", updatedCategory);

        chatTempDataService.clearAndSave(chatId,
                ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.GET_CATEGORY_INFO_RESPONSE)
                        .data(new Gson().toJson(updatedCategory))
                        .build());

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.RENAME_CATEGORY_RESPONSE)
                .build();
    }
}