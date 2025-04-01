package com.sergio.memo_bot.util;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.menubutton.SetChatMenuButton;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButton;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;

public class BotReplyMapper {

    public static BotApiMethod<?> toBotApiMethod(BotReply reply) {
        return switch (reply.getType()) {
            case MESSAGE, FORCE_REPLY -> SendMessage.builder()
                    .chatId(reply.getChatId())
                    .text(reply.getText())
                    .replyMarkup(reply.getReplyMarkup())
                    .parseMode(reply.getParseMode())
                    .build();
            case EDIT_MESSAGE_TEXT -> EditMessageText.builder()
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .text(reply.getText())
                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                    .parseMode(reply.getParseMode())
                    .build();
            case EDIT_MESSAGE_REPLY_MARKUP -> EditMessageReplyMarkup.builder()
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
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

    public static BotApiMethod<?> toBotApiMethod(MultipleBotReply reply) {
        return switch (reply.getType()) {
            case MESSAGE, FORCE_REPLY -> SendMessage.builder()
                    .chatId(reply.getChatId())
                    .text(reply.getText())
                    .replyMarkup(reply.getReplyMarkup())
                    .parseMode(reply.getParseMode())
                    .build();
            case EDIT_MESSAGE_TEXT -> EditMessageText.builder()
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .text(reply.getText())
                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                    .parseMode(reply.getParseMode())
                    .build();
            case EDIT_MESSAGE_REPLY_MARKUP -> EditMessageReplyMarkup.builder()
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
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
