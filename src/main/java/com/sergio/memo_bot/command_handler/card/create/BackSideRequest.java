package com.sergio.memo_bot.command_handler.card.create;

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
public class BackSideRequest implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.INSERT_BACK_SIDE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        chatAwaitsInputService.update(processableMessage.getChatId(), CommandType.BACK_SIDE_RECEIVED);

        return BotMessageReply.builder()
//                .type(BotReplyType.MESSAGE)
                .text("Введите заднюю сторону карточки")
//                .messageId(processableMessage.getMessageId())
                .chatId(processableMessage.getChatId())
//                .replyMarkup(MarkUpUtil.getForceReplyMarkup("Задняя сторона"))
                .build();
    }
}
