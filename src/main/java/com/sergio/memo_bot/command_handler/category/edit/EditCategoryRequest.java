package com.sergio.memo_bot.command_handler.category.edit;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditCategoryRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_CATEGORY_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CategoryDto categoryDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE, CategoryDto.class);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(EDIT_CATEGORY.formatted(categoryDto.getTitle()))
                .parseMode(ParseMode.HTML)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(RENAME_CATEGORY, CommandType.RENAME_CATEGORY_REQUEST),
                        Pair.of(DELETE_CATEGORY, CommandType.DELETE_CATEGORY_REQUEST),
                        Pair.of(BACK, CommandType.GET_CATEGORY_INFO_RESPONSE)
                )))
                .build();
    }

}