package com.sergio.memo_bot.command_handler.category.edit.rename;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CATEGORY_SUCCESSFULLY_RENAMED;

@Slf4j
@Component
@RequiredArgsConstructor
public class RenameCategoryResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.RENAME_CATEGORY_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        CategoryDto categoryDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE, CategoryDto.class);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CATEGORY_SUCCESSFULLY_RENAMED.formatted(categoryDto.getTitle()))
                .parseMode(ParseMode.HTML)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.GET_CATEGORY_INFO_RESPONSE)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();
    }
}