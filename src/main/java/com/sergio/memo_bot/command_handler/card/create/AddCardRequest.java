package com.sergio.memo_bot.command_handler.card.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.LETS_ADD_CARD;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.LETS_ADD_FIRST_CARD;


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
                .text(processableMessage.isFromPartReply()
                        ? processableMessage.getText() + LETS_ADD_FIRST_CARD
                        : LETS_ADD_CARD)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.INSERT_FRONT_SIDE)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .parseMode(ParseMode.HTML)
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
