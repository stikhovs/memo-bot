package com.sergio.memo_bot.mapper;

import com.sergio.memo_bot.command_handler.exercise.poll.dto.QuizItem;
import com.sergio.memo_bot.persistence.entity.ChatMessage;
import com.sergio.memo_bot.persistence.service.ChatMessageService;
import com.sergio.memo_bot.service.ReplyData;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.BotQuizReply;
import com.sergio.memo_bot.util.DeleteMessageReply;
import com.sergio.memo_bot.util.SenderType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.polls.input.InputPollOption;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyMapper {

    private final ChatMessageService chatMessageService;

    public List<ReplyData> toReplyData(BotMessageReply reply) {
        ArrayList<ReplyData> replies = new ArrayList<>();
        Long chatId = reply.getChatId();

        Optional<ChatMessage> lastMessageOptional = chatMessageService.findLastMessage(chatId);

        if (lastMessageOptional.isPresent()) {
            ChatMessage lastMessage = lastMessageOptional.get();
            Optional<ChatMessage> lastWithButtonsMessage = chatMessageService.findLastWithButtonsMessage(chatId);

            if (lastMessage.getSenderType() == SenderType.USER) {
                lastWithButtonsMessage.ifPresent(lastWithButtons -> replies.add(
                        ReplyData.builder()
                                .reply(
                                        EditMessageReplyMarkup.builder()
                                                .chatId(chatId)
                                                .messageId(lastWithButtons.getMessageId())
                                                .replyMarkup(null)
                                                .build()
                                )
                                .chatId(chatId)
                                .messageId(lastWithButtons.getMessageId())
                                .hasButtons(false)
                                .build()
                ));

                replies.add(
                        ReplyData.builder()
                                .reply(SendMessage.builder()
                                        .chatId(chatId)
                                        .text(reply.getText())
                                        .parseMode(reply.getParseMode())
                                        .replyMarkup(reply.getReplyMarkup())
                                        .build())
                                .hasButtons(reply.getReplyMarkup() != null)
                                .chatId(chatId)
                                .build()
                );
            } else {
                if (lastMessage.isHasButtons()) {
                    replies.add(
                            ReplyData.builder()
                                    .reply(
                                            EditMessageText.builder()
                                                    .chatId(chatId)
                                                    .messageId(lastMessage.getMessageId())
                                                    .text(reply.getText())
                                                    .parseMode(reply.getParseMode())
                                                    .replyMarkup(
                                                            reply.getReplyMarkup() != null && reply.getReplyMarkup() instanceof InlineKeyboardMarkup
                                                                    ? (InlineKeyboardMarkup) reply.getReplyMarkup()
                                                                    : null
                                                            )
                                                    .build()
                                    )
                                    .chatId(chatId)
                                    .messageId(lastMessage.getMessageId())
                                    .hasButtons(reply.getReplyMarkup() != null)
                                    .build()
                    );
                } else {
                    replies.add(
                            ReplyData.builder()
                                    .reply(SendMessage.builder()
                                            .chatId(chatId)
                                            .text(reply.getText())
                                            .parseMode(reply.getParseMode())
                                            .replyMarkup(reply.getReplyMarkup())
                                            .build())
                                    .hasButtons(reply.getReplyMarkup() != null)
                                    .chatId(chatId)
                                    .build()
                    );
                }
            }

        } else {
            replies.add(
                    ReplyData.builder()
                            .reply(SendMessage.builder()
                                    .chatId(chatId)
                                    .text(reply.getText())
                                    .parseMode(reply.getParseMode())
                                    .replyMarkup(reply.getReplyMarkup())
                                    .build())
                            .hasButtons(reply.getReplyMarkup() != null)
                            .chatId(chatId)
                            .build()
            );
        }

        return replies;
    }

    public ReplyData toReplyData(BotQuizReply reply) {
        QuizItem quiz = reply.getQuiz();
        return ReplyData.builder()
                .reply(SendPoll.builder()
                        .chatId(reply.getChatId())
                        .type("quiz")
                        .protectContent(true)
                        .isAnonymous(false)
                        .question(quiz.getQuestion())
                        .options(quiz.getAnswerOptions().stream().map(option -> InputPollOption.builder().text(option.getFirst()).build()).toList())
                        .correctOptionId(reply.getCorrectIndex())
                        .build())
                .hasButtons(false)
                .build();
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
