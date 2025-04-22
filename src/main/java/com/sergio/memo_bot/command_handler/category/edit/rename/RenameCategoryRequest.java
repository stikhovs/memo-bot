package com.sergio.memo_bot.command_handler.category.edit.rename;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.INSERT_CATEGORY_NEW_TITLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RenameCategoryRequest implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.RENAME_CATEGORY_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        chatAwaitsInputService.save(processableMessage.getChatId(), CommandType.RENAME_CATEGORY_USER_INPUT_TITLE);

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(INSERT_CATEGORY_NEW_TITLE)
                .build();
    }
}