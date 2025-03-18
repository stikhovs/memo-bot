package com.sergio.memo_bot.util;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class BotReplyMapper {

    public static BotApiMethod<?> toBotApiMethod(BotReply reply) {
        return switch (reply.getType()) {
            case MESSAGE, FORCE_REPLY -> SendMessage.builder()
                    .chatId(reply.getChatId())
                    .text(reply.getText())
                    .replyMarkup(reply.getReplyMarkup())
                    .build();
            case EDIT_MESSAGE_TEXT -> EditMessageText.builder()
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .text(reply.getText())
                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                    .build();
            case EDIT_MESSAGE_REPLY_MARKUP -> EditMessageReplyMarkup.builder()
                    .chatId(reply.getChatId())
                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                    .messageId(reply.getMessageId())
                    .build();
            case DELETE_MESSAGE_TEXT -> DeleteMessage.builder()
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
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
