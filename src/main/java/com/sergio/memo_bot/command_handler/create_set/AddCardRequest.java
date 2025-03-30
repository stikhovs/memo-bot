package com.sergio.memo_bot.command_handler.create_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class AddCardRequest implements CommandHandler {

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.ADD_CARD_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        if (processableMessage.isFromPartReply()) {
            return MultipleBotReply.builder()
                    .type(BotReplyType.MESSAGE)
                    .nextCommand(CommandType.INSERT_FRONT_SIDE)
                    .previousProcessableMessage(processableMessage)
                    .chatId(processableMessage.getChatId())
                    .text(processableMessage.getText() + "\nТеперь давайте добавим в него карточки")
                    .build();
        }

        return BotReply.builder()
                .type(BotReplyType.EDIT_MESSAGE_REPLY_MARKUP)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .nextReply(
                        MultipleBotReply.builder()
                                .type(BotReplyType.MESSAGE)
                                .messageId(processableMessage.getMessageId())
                                .nextCommand(CommandType.INSERT_FRONT_SIDE)
                                .previousProcessableMessage(processableMessage)
                                .chatId(processableMessage.getChatId())
                                .text("Добавим еще одну карточку")
                                .build()
                )
                .build();
    }
}
