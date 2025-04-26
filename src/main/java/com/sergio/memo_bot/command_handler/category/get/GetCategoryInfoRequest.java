package com.sergio.memo_bot.command_handler.category.get;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCategoryInfoRequest implements CommandHandler {

    private final CategoryHttpService categoryHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_CATEGORY_INFO_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        String[] commandAndCategoryId = processableMessage.getText().split("__");
        Long categoryId = Long.valueOf(commandAndCategoryId[1]);

        CategoryDto category = categoryHttpService.getById(categoryId);

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.GET_CATEGORY_INFO_RESPONSE)
                        .data(new Gson().toJson(category))
                .build());

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.GET_CATEGORY_CARD_SET_INFO)
                .build();
    }
}