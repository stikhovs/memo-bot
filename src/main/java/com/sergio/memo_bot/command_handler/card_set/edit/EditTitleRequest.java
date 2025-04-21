package com.sergio.memo_bot.command_handler.card_set.edit;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditTitleRequest implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_TITLE_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        chatAwaitsInputService.clearAndSave(processableMessage.getChatId(), CommandType.EDIT_TITLE_RESPONSE);

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
//                .messageId(processableMessage.getMessageId())
//                .type(BotReplyType.MESSAGE)
                .text("Введите новое название набора")
                .build();
    }
}
