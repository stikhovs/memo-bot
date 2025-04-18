package com.sergio.memo_bot.command_handler.card.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.NextReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


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

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(processableMessage.isFromPartReply() ?
                        processableMessage.getText() + "\n\nТеперь давайте добавим в него карточки"
                        : "Добавим карточку")
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.INSERT_FRONT_SIDE)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();

        /*if (processableMessage.isFromPartReply()) {
            return BotMessageReply.builder()
//                    .type(BotReplyType.MESSAGE)
                    .chatId(processableMessage.getChatId())
                    .text(processableMessage.getText() + "\n\nТеперь давайте добавим в него карточки")
                    .nextReply(NextReply.builder()
                            .nextCommand(CommandType.INSERT_FRONT_SIDE)
                            .previousProcessableMessage(processableMessage)
                            .build())
                    .build();
        }

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Добавим карточку")
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.INSERT_FRONT_SIDE)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();*/

        /*BotReply.builder()
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
                                .text("Добавим карточку")
                                .build()
                )
                .build();*/
    }
}
