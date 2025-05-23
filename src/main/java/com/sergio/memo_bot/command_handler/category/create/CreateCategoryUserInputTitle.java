package com.sergio.memo_bot.command_handler.category.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.DUPLICATED_CATEGORY;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.STRING_IS_TOO_LONG;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCategoryUserInputTitle implements CommandHandler {

    private final CategoryHttpService categoryHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CREATE_CATEGORY_USER_INPUT_TITLE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        String categoryTitle = processableMessage.getText();

        if (isBlank(categoryTitle) || length(categoryTitle) > 100) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(STRING_IS_TOO_LONG)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.CREATE_CATEGORY_REQUEST)
                            .build())
                    .build();
        }

        List<CategoryDto> existingCategories = categoryHttpService.getByChatId(chatId);
        Optional<CategoryDto> duplicatedCategory = existingCategories.stream().filter(categoryDto -> categoryDto.getTitle().equalsIgnoreCase(categoryTitle))
                .findFirst();

        if (duplicatedCategory.isPresent()) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(DUPLICATED_CATEGORY)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.CREATE_CATEGORY_REQUEST)
                            .build())
                    .build();
        }

        CategoryDto categoryDto = categoryHttpService.save(chatId, CategoryDto.builder()
                .title(categoryTitle)
                .build());

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .command(CommandType.CREATE_CATEGORY_RESPONSE)
                .data(categoryDto.getTitle())
                .build());

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.CREATE_CATEGORY_RESPONSE)
                .build();
    }
}