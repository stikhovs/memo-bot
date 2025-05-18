package com.sergio.memo_bot.persistence.service;

import com.sergio.memo_bot.configuration.BotProperties;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.feedback.Feedback;
import com.sergio.memo_bot.persistence.entity.feedback.FeedbackStatus;
import com.sergio.memo_bot.persistence.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BotProperties botProperties;
    private final FeedbackRepository feedbackRepository;

    public Feedback saveNewFeedback(ProcessableMessage processableMessage, Integer messageId) {
        log.info("Saving new feedback from chatId {}", processableMessage.getChatId());
        Feedback feedback = Feedback.builder()
                .sourceChatId(processableMessage.getChatId())
                .adminChatId(botProperties.getAdminChatId())
                .messageId(messageId)
                .status(FeedbackStatus.NEW)
                .build();

        return feedbackRepository.save(feedback);
    }

    public Optional<Feedback> findFeedbackByMessageId(Integer messageId) {
        return feedbackRepository.findByMessageId(messageId);
    }

    public void setAsResolved(Feedback feedback) {
        log.info("Setting feedback as RESOLVED. Feedback: id {}, chatId {}, messageId {}", feedback.getId(), feedback.getSourceChatId(), feedback.getMessageId());
        feedbackRepository.save(feedback.toBuilder().status(FeedbackStatus.RESOLVED).build());
    }

    public void setAsError(Feedback feedback) {
        log.warn("Setting feedback as ERROR. Feedback: id {}, chatId {}, messageId {}", feedback.getId(), feedback.getSourceChatId(), feedback.getMessageId());
        feedbackRepository.save(feedback.toBuilder().status(FeedbackStatus.ERROR).build());
    }

    public void deleteAllResolvedFeedbacks() {
        feedbackRepository.deleteByStatus(FeedbackStatus.RESOLVED);
    }

}