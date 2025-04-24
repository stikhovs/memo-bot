package com.sergio.memo_bot.command_handler.card_set.create;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.SET_WILL_BE_CREATED_WITH_CATEGORY;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetCategoryResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SET_CATEGORY_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        String[] commandAndCategoryId = processableMessage.getText().split("__");
        Long categoryId = Long.valueOf(commandAndCategoryId[1]);

        List<CategoryDto> categories = chatTempDataService.mapDataToList(chatId, CommandType.SET_CATEGORY_REQUEST, CategoryDto.class);

        CategoryDto chosenCategory = categories.stream().filter(it -> it.getId().equals(categoryId)).findFirst().orElseThrow();

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.GET_CATEGORY_INFO_RESPONSE)
                        .data(new Gson().toJson(chosenCategory))
                .build());

        if (chosenCategory.isDefault()) {
            return BotPartReply.builder()
                    .chatId(chatId)
                    .previousProcessableMessage(processableMessage)
                    .nextCommand(CommandType.NAME_SET_REQUEST)
                    .build();
        }

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(SET_WILL_BE_CREATED_WITH_CATEGORY.formatted(chosenCategory.getTitle()))
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.NAME_SET_REQUEST)
                        .build())
                .parseMode(ParseMode.HTML)
                .build();
    }
}