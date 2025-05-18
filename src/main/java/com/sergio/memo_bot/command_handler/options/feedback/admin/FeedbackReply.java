package com.sergio.memo_bot.command_handler.options.feedback.admin;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.feedback.Feedback;
import com.sergio.memo_bot.persistence.service.FeedbackService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.service.FeedbackSenderService;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackReply implements CommandHandler {

    private final FeedbackService feedbackService;
    private final FeedbackSenderService feedbackSenderService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.FEEDBACK_REPLY == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        Integer replyToMessageId = processableMessage.getReplyToMessageId();

        Feedback feedback = feedbackService.findFeedbackByMessageId(replyToMessageId)
                .orElseThrow(() -> new RuntimeException("Couldn't find feedback by replyToMessageId %s in the database".formatted(replyToMessageId)));

        feedbackSenderService.sendReplyToFeedback(feedback, processableMessage.getText());

        return BotMessageReply.builder()
                .chatId(chatId)
                .text("Ответ отправлен")
                .build();
    }
}