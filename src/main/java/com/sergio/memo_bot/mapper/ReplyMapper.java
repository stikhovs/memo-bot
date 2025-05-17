package com.sergio.memo_bot.mapper;

import com.sergio.memo_bot.command_handler.exercise.quiz.dto.QuizItem;
import com.sergio.memo_bot.persistence.entity.ChatMessage;
import com.sergio.memo_bot.persistence.entity.MessageContentType;
import com.sergio.memo_bot.persistence.entity.SenderType;
import com.sergio.memo_bot.persistence.service.ChatMessageService;
import com.sergio.memo_bot.reply.BotImageReply;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotQuizReply;
import com.sergio.memo_bot.reply.DeleteMessageReply;
import com.sergio.memo_bot.service.ReplyData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.polls.input.InputPollOption;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.QUIZ_START;

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
                                .hasButtons(hasInlineKeyboard(reply))
                                .chatId(chatId)
                                .build()
                );
            } else {
                if (lastMessage.isHasButtons()) {
                    if (lastMessage.getMessageContentType() == MessageContentType.TEXT) {
                        replies.add(
                                ReplyData.builder()
                                        .reply(
                                                EditMessageText.builder()
                                                        .chatId(chatId)
                                                        .messageId(lastMessage.getMessageId())
                                                        .text(reply.getText())
                                                        .parseMode(reply.getParseMode())
                                                        .replyMarkup(
                                                                hasInlineKeyboard(reply)
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
                                        .reply(
                                                DeleteMessage.builder()
                                                        .chatId(chatId)
                                                        .messageId(lastMessage.getMessageId())
                                                        .build()
                                        )
                                        .chatId(chatId)
                                        .messageId(lastMessage.getMessageId())
                                        .hasButtons(hasInlineKeyboard(reply))
                                        .build()
                        );
                        replies.add(
                                ReplyData.builder()
                                        .reply(SendMessage.builder()
                                                .chatId(chatId)
                                                .text(reply.getText())
                                                .parseMode(reply.getParseMode())
                                                .replyMarkup(reply.getReplyMarkup())
                                                .build())
                                        .hasButtons(hasInlineKeyboard(reply))
                                        .chatId(chatId)
                                        .build()
                        );
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
                                    .hasButtons(hasInlineKeyboard(reply))
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
                            .hasButtons(hasInlineKeyboard(reply))
                            .chatId(chatId)
                            .build()
            );
        }

        return replies;
    }

    public List<ReplyData> toReplyData(BotQuizReply reply) {
        List<ReplyData> replies = new ArrayList<>();
        Long chatId = reply.getChatId();

        QuizItem quiz = reply.getQuiz();
        Optional<ChatMessage> lastWithButtonsMessage = chatMessageService.findLastWithButtonsMessage(chatId);
        if (lastWithButtonsMessage.isPresent()) {
            ChatMessage lastMessage = lastWithButtonsMessage.get();
            replies.add(
                    ReplyData.builder()
                            .reply(
                                    EditMessageText.builder()
                                            .chatId(chatId)
                                            .messageId(lastMessage.getMessageId())
                                            .text(QUIZ_START)
                                            .replyMarkup(null)
                                            .build()
                            )
                            .chatId(chatId)
                            .messageId(lastMessage.getMessageId())
                            .hasButtons(false)
                            .build()
            );
        }
        replies.add(
                ReplyData.builder()
                        .reply(SendPoll.builder()
                                .chatId(chatId)
                                .type("quiz")
                                .protectContent(true)
                                .isAnonymous(false)
                                .question(quiz.getQuestion())
                                .options(quiz.getAnswerOptions().stream().map(option -> InputPollOption.builder().text(option.getFirst()).build()).toList())
                                .correctOptionId(reply.getCorrectIndex())
                                .build())
                        .hasButtons(false)
                        .chatId(chatId)
                        .build()
        );
        return replies;
    }

    public ReplyData toReplyData(BotImageReply reply) {
        Long chatId = reply.getChatId();

        Optional<ChatMessage> lastMessageOptional = chatMessageService.findLastWithButtonsMessage(chatId);

        if (lastMessageOptional.isPresent()) {
            Integer messageId = lastMessageOptional.get().getMessageId();
            return ReplyData.builder()
                    .reply(EditMessageMedia.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .media(InputMediaPhoto.builder()
                                    .media(reply.getImage(), reply.getFileName())
                                    .caption(reply.getCaption())
                                    .showCaptionAboveMedia(true)
                                    .parseMode(reply.getParseMode())
                                    .build())
                            .replyMarkup((InlineKeyboardMarkup) reply.getReplyMarkup())
                            .build())
                    .hasButtons(reply.getReplyMarkup() != null)
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();
        }


        InputFile imgFile = new InputFile(reply.getImage(), reply.getFileName());

        return ReplyData.builder()
                .reply(SendPhoto.builder()
                        .chatId(chatId)
                        .photo(imgFile)
                        .caption(reply.getCaption())
                        .showCaptionAboveMedia(true)
                        .parseMode(reply.getParseMode())
                        .replyMarkup(reply.getReplyMarkup())
                        .build())
                .hasButtons(reply.getReplyMarkup() != null)
                .chatId(chatId)
                .build();
    }

    public ReplyData toReplyData(DeleteMessageReply reply) {
        if (CollectionUtils.size(reply.getMessageIds()) > 1) {
            return ReplyData.builder()
                    .chatId(reply.getChatId())
                    .reply(DeleteMessages.builder()
                            .chatId(reply.getChatId())
                            .messageIds(reply.getMessageIds())
                            .build())
                    .hasButtons(false)
                    .messageIds(reply.getMessageIds())
                    .build();
        } else {
            return ReplyData.builder()
                    .chatId(reply.getChatId())
                    .reply(DeleteMessage.builder()
                            .chatId(reply.getChatId())
                            .messageId(reply.getMessageIds().getFirst())
                            .build())
                    .hasButtons(false)
                    .messageId(reply.getMessageIds().getFirst())
                    .build();
        }
    }

    private boolean hasInlineKeyboard(BotMessageReply reply) {
        return reply.getReplyMarkup() != null && reply.getReplyMarkup() instanceof InlineKeyboardMarkup;
    }


}
