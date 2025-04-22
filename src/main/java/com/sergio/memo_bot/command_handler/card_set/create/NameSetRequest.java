package com.sergio.memo_bot.command_handler.card_set.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.WHAT_IS_THE_NAME_OF_THE_SET;

@Slf4j
@Component
@RequiredArgsConstructor
public class NameSetRequest implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.NAME_SET_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        chatAwaitsInputService.clearAndSave(processableMessage.getChatId(), CommandType.NAME_SET_RESPONSE);

        return BotMessageReply.builder()
                .text(WHAT_IS_THE_NAME_OF_THE_SET)
                .chatId(processableMessage.getChatId())
                .build();
    }
}
