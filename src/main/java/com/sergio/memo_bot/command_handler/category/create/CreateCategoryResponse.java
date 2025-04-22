package com.sergio.memo_bot.command_handler.category.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CATEGORY_SUCCESSFULLY_CREATED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCategoryResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CREATE_CATEGORY_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        String categoryTitle = chatTempDataService.get(processableMessage.getChatId(), CommandType.CREATE_CATEGORY_RESPONSE)
                .getData();

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(CATEGORY_SUCCESSFULLY_CREATED.formatted(categoryTitle))
                .parseMode(ParseMode.HTML)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.MAIN_MENU)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();
    }
}