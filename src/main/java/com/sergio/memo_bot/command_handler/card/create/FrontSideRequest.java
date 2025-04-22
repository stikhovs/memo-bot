package com.sergio.memo_bot.command_handler.card.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.INSERT_FRONT_SIDE;


@Slf4j
@Component
@RequiredArgsConstructor
public class FrontSideRequest implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.INSERT_FRONT_SIDE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        chatAwaitsInputService.clearAndSave(processableMessage.getChatId(), CommandType.FRONT_SIDE_RECEIVED);

        return BotMessageReply.builder()
                .text(INSERT_FRONT_SIDE)
                .chatId(processableMessage.getChatId())
                .build();
    }
}
