package com.sergio.memo_bot.command_handler.category.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.INSERT_CATEGORY_TITLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCategoryRequest implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CREATE_CATEGORY_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        chatAwaitsInputService.save(processableMessage.getChatId(), CommandType.CREATE_CATEGORY_USER_INPUT_TITLE);

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(INSERT_CATEGORY_TITLE)
                .build();
    }
}