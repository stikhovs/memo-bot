package com.sergio.memo_bot.command_handler.category.delete;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteCategoryResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.DELETE_CATEGORY_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        chatTempDataService.clear(processableMessage.getChatId(), CommandType.CATEGORY_MENU_DATA);
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Категория удалена")
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.CATEGORY_MENU_DATA)
                        .build())
                .build();
    }
}