package com.sergio.memo_bot.mapper;

import com.sergio.memo_bot.service.ReplyData;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.DeleteMessageReply;
import com.sergio.memo_bot.util.MultipleBotReply;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class ReplyMapper {

    public ReplyData toReplyData(BotReply reply) {
        final boolean hasButtons = reply.getReplyMarkup() != null;

        return switch (reply.getType()) {
            case MESSAGE, FORCE_REPLY -> ReplyData.builder()
                    .reply(SendMessage.builder()
                            .chatId(reply.getChatId())
                            .text(reply.getText())
                            .replyMarkup(reply.getReplyMarkup())
                            .parseMode(reply.getParseMode())
                            .build())
                    .hasButtons(hasButtons)
                    .build();
            case EDIT_MESSAGE_TEXT -> ReplyData.builder()
                    .reply(
                            EditMessageText.builder()
                                    .chatId(reply.getChatId())
                                    .messageId(reply.getMessageId())
                                    .text(reply.getText())
                                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                                    .parseMode(reply.getParseMode())
                                    .build()
                    )
                    .hasButtons(hasButtons)
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .build();
            case EDIT_MESSAGE_REPLY_MARKUP -> ReplyData.builder()
                    .reply(
                            EditMessageReplyMarkup.builder()
                                    .chatId(reply.getChatId())
                                    .messageId(reply.getMessageId())
                                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                                    .build()
                    )
                    .hasButtons(hasButtons)
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .build();
            case POLL -> ReplyData.builder()
                    .reply(
                            SendPoll.builder()
                                    .chatId(reply.getChatId())
                                    .isAnonymous(false)
                                    .allowMultipleAnswers(false)
                                    .question(reply.getText())
//                      .type()
                                    .protectContent(true)
//                      .options()
                                    .build()
                    )
                    .hasButtons(hasButtons)
                    .build();
            default -> throw new IllegalArgumentException("Couldn't map reply of type [%s]".formatted(reply.getType()));
        };
    }

    public ReplyData toReplyData(MultipleBotReply reply) {
        final boolean hasButtons = reply.getReplyMarkup() != null;

        return switch (reply.getType()) {
            case MESSAGE, FORCE_REPLY -> ReplyData.builder()
                    .reply(SendMessage.builder()
                            .chatId(reply.getChatId())
                            .text(reply.getText())
                            .replyMarkup(reply.getReplyMarkup())
                            .parseMode(reply.getParseMode())
                            .build())
                    .hasButtons(hasButtons)
                    .build();
            case EDIT_MESSAGE_TEXT -> ReplyData.builder()
                    .reply(
                            EditMessageText.builder()
                                    .chatId(reply.getChatId())
                                    .messageId(reply.getMessageId())
                                    .text(reply.getText())
                                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                                    .parseMode(reply.getParseMode())
                                    .build()
                    )
                    .hasButtons(hasButtons)
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .build();
            case EDIT_MESSAGE_REPLY_MARKUP -> ReplyData.builder()
                    .reply(
                            EditMessageReplyMarkup.builder()
                                    .chatId(reply.getChatId())
                                    .messageId(reply.getMessageId())
                                    .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                                    .build()
                    )
                    .hasButtons(hasButtons)
                    .chatId(reply.getChatId())
                    .messageId(reply.getMessageId())
                    .build();
            case POLL -> ReplyData.builder()
                    .reply(
                            SendPoll.builder()
                                    .chatId(reply.getChatId())
                                    .isAnonymous(false)
                                    .allowMultipleAnswers(false)
                                    .question(reply.getText())
//                      .type()
                                    .protectContent(true)
//                      .options()
                                    .build()
                    )
                    .hasButtons(hasButtons)
                    .build();
            default -> throw new IllegalArgumentException("Couldn't map reply of type [%s]".formatted(reply.getType()));
        };
    }

    public ReplyData toReplyData(DeleteMessageReply reply) {
        if (CollectionUtils.size(reply.getMessageIds()) > 1) {
            return ReplyData.builder()
                    .reply(DeleteMessages.builder()
                            .chatId(reply.getChatId())
                            .messageIds(reply.getMessageIds())
                            .build())
                    .hasButtons(false)
                    .build();
        } else {
            return ReplyData.builder()
                    .reply(DeleteMessage.builder()
                            .chatId(reply.getChatId())
                            .messageId(reply.getMessageIds().getFirst())
                            .build())
                    .hasButtons(false)
                    .build();
        }
    }


}
