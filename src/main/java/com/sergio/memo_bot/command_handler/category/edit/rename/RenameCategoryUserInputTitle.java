package com.sergio.memo_bot.command_handler.category.edit.rename;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.STRING_IS_TOO_LONG;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

@Slf4j
@Component
@RequiredArgsConstructor
public class RenameCategoryUserInputTitle implements CommandHandler {

    private final CategoryHttpService categoryHttpService;
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

        if (isBlank(categoryTitle) || length(categoryTitle) > 100) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(STRING_IS_TOO_LONG)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.RENAME_CATEGORY_REQUEST)
                            .build())
                    .build();
        }

        CategoryDto categoryDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE, CategoryDto.class);


        CategoryDto updatedCategory = categoryHttpService.update(categoryDto.toBuilder()
                .title(categoryTitle)
                .build());

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