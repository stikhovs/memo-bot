package com.sergio.memo_bot.command_handler.card.edit;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditBackSideRequest implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EDIT_CARD_BACK_SIDE_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        chatAwaitsInputService.clearAndSave(processableMessage.getChatId(), CommandType.EDIT_CARD_BACK_SIDE_RESPONSE);

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
//                .type(BotReplyType.MESSAGE)
                .text("Введите обратную сторону")
//                .messageId(processableMessage.getMessageId())
                .build();
    }
}
