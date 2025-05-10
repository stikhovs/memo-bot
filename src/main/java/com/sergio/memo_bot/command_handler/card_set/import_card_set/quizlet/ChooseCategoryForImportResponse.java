package com.sergio.memo_bot.command_handler.card_set.import_card_set.quizlet;

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

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CHOSEN_CATEGORY;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseCategoryForImportResponse implements CommandHandler {

    private final CategoryHttpService categoryHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CHOOSE_CATEGORY_FOR_IMPORT_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        String[] commandAndCategoryId = processableMessage.getText().split("__");
        String categoryId = commandAndCategoryId[1];

        CategoryDto category = categoryHttpService.getById(Long.valueOf(categoryId));

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.CHOOSE_CATEGORY_FOR_IMPORT_RESPONSE)
                        .data(categoryId)
                .build());

        if (category.isDefault()) {
            return BotPartReply.builder()
                    .chatId(chatId)
                    .previousProcessableMessage(processableMessage)
                    .nextCommand(CommandType.IMPORT_CARD_SET_TITLE_REQUEST)
                    .build();
        }

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOSEN_CATEGORY.formatted(category.getTitle()))
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.IMPORT_CARD_SET_TITLE_REQUEST)
                        .build())
                .build();
    }
}