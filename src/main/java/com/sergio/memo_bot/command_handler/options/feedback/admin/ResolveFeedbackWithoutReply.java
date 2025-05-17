package com.sergio.memo_bot.command_handler.options.feedback.admin;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.feedback.Feedback;
import com.sergio.memo_bot.persistence.service.FeedbackService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResolveFeedbackWithoutReply implements CommandHandler {
    private final FeedbackService feedbackService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.RESOLVE_FEEDBACK_WITHOUT_REPLY == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        Integer messageId = processableMessage.getMessageId();

        Feedback feedback = feedbackService.findFeedbackByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("Couldn't find feedback by messageId %s in the database".formatted(messageId)));

        feedbackService.setAsResolved(feedback);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text("Resolved")
                .build();
    }
}