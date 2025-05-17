package com.sergio.memo_bot.service;

import com.sergio.memo_bot.configuration.BotProperties;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.MessageContentType;
import com.sergio.memo_bot.persistence.entity.feedback.Feedback;
import com.sergio.memo_bot.persistence.service.ChatMessageService;
import com.sergio.memo_bot.persistence.service.FeedbackService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Comparator;
import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.FEEDBACK_REPLY_FROM_ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackSenderService {

    private final BotProperties botProperties;
    private final TelegramClient memoBotClient;
    private final FeedbackService feedbackService;
    private final ChatMessageService chatMessageService;

    @SneakyThrows
    public void sendFeedbackToAdminChat(ProcessableMessage processableMessage) {
        log.info("Sending feedback from chatId {}", processableMessage.getChatId());
        Long adminChatId = botProperties.getAdminChatId();

        String feedbackIntro = "Фидбек от пользователя %s\n\n".formatted(processableMessage.getChatId());
        String feedbackPayload = "Содержимое: \n\n";
        String feedbackText = feedbackIntro + feedbackPayload + processableMessage.getText();

        Message sentMessage;

        if (processableMessage.isHasPhoto()) {

            String photo_id = processableMessage.getPhotos().stream().max(Comparator.comparing(PhotoSize::getFileSize))
                    .map(PhotoSize::getFileId)
                    .orElse("");

            sentMessage = memoBotClient.execute(
                    SendPhoto.builder()
                            .chatId(adminChatId)
                            .caption(feedbackText)
                            .photo(new InputFile(photo_id))
                            .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                    Pair.of("Отметить как решенное", CommandType.RESOLVE_FEEDBACK_WITHOUT_REPLY)
                            )))
                            .build());
        } else {
            sentMessage = memoBotClient.execute(
                    SendMessage.builder()
                            .chatId(adminChatId)
                            .text(feedbackText)
                            .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                    Pair.of("Отметить как решенное", CommandType.RESOLVE_FEEDBACK_WITHOUT_REPLY)
                            )))
                            .build()
            );
        }

        Feedback feedback = feedbackService.saveNewFeedback(processableMessage, sentMessage.getMessageId());
        log.info("Feedback from chatId {} was successfully sent. Feedback id: {}", processableMessage.getChatId(), feedback.getId());
    }

    @Transactional
    public void sendReplyToFeedback(Feedback feedback, String textReply) {
        log.info("Reply to feedback with id {} will be sent to chatId {}", feedback.getId(), feedback.getSourceChatId());
        try {
            Message sentMessage = memoBotClient.execute(
                    SendMessage.builder()
                            .chatId(feedback.getSourceChatId())
                            .text(FEEDBACK_REPLY_FROM_ADMIN + textReply)
                            .build());

            feedbackService.setAsResolved(feedback);
            chatMessageService.saveFromBot(feedback.getSourceChatId(), sentMessage.getMessageId(), false, MessageContentType.TEXT);
            log.info("Reply to feedback with id {} was successfully sent to chatId {}. Feedback resolved.", feedback.getId(), feedback.getSourceChatId());
        } catch (TelegramApiException e) {
            log.error("Couldn't send reply to feedback with id %s to chatId %s".formatted(feedback.getId(), feedback.getSourceChatId()), e);
            feedbackService.setAsError(feedback);
        }
    }

}
