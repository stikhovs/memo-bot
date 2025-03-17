package com.sergio.memo_bot.util;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class BotReplyMapper {

    public static BotApiMethod<?> toBotApiMethod(BotReply reply) {
        return switch (reply.getType()) {
            case MESSAGE -> SendMessage.builder()
                    .chatId(reply.getChatId())
                    .text(reply.getText())
                    .replyMarkup(reply.getReplyMarkup())
                    .build();
            case POLL -> SendPoll.builder()
                    .chatId(reply.getChatId())
                    .isAnonymous(false)
                    .allowMultipleAnswers(false)
                    .question(reply.getText())
//                      .type()
                    .protectContent(true)
//                      .options()
                    .build();
            default -> throw new IllegalArgumentException("Couldn't map reply of type [%s]".formatted(reply.getType()));
        };
    }

}
