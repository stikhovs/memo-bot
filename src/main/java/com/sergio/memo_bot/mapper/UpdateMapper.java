package com.sergio.memo_bot.mapper;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateMapper {

    public ProcessableMessage map(Update update) {
        if (isText(update)) {
            Message message = update.getMessage();
            User user = message.getFrom();
            Integer messageId = message.getMessageId();

            ProcessableMessage processableMessage = ProcessableMessage.builder()
                    .processable(isFromUser(user))
                    .username(user.getUserName())
                    .userId(user.getId())
                    .chatId(message.getChatId())
                    .text(message.getText())
                    .messageId(messageId)
                    .build();

            if (message.getReplyToMessage() != null) {
                processableMessage.setReplyToMessageId(message.getReplyToMessage().getMessageId());
            }

            return processableMessage;
        }

        if (isCallbackData(update)) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            User user = callbackQuery.getFrom();
            MaybeInaccessibleMessage message = callbackQuery.getMessage();
            Integer messageId = message.getMessageId();

            boolean hasPhoto = hasPhoto(callbackQuery);

            return ProcessableMessage.builder()
                    .processable(isFromUser(user))
                    .username(user.getUserName())
                    .userId(user.getId())
                    .chatId(message.getChatId())
                    .text(callbackQuery.getData())
                    .messageId(messageId)
                    .hasPhoto(hasPhoto)
                    .build();
        }

        if (isEditedMessage(update)) {
            Message message = update.getEditedMessage();
            User user = message.getFrom();
            Integer messageId = message.getMessageId();
            return ProcessableMessage.builder()
                    .processable(isFromUser(user))
                    .username(user.getUserName())
                    .userId(user.getId())
                    .chatId(message.getChatId())
                    .text(message.getText())
                    .messageId(messageId)
                    .build();
        }

        if (isPoll(update)) {
            Poll poll = update.getPoll();
            PollOption answer = poll.getOptions()
                    .stream()
                    .filter(it -> it.getVoterCount() == 1)
                    .findFirst()
                    .orElseThrow();
            return ProcessableMessage.builder()
                    .processable(true)
                    .text(answer.getText())
                    .build();
        }

        if (isPollAnswer(update)) {
            PollAnswer pollAnswer = update.getPollAnswer();
            User user = pollAnswer.getUser();
            return ProcessableMessage.builder()
                    .processable(isFromUser(user))
                    .username(user.getUserName())
                    .userId(user.getId())
                    .chatId(user.getId())
                    .text(CommandType.QUIZ.getCommandText())
                    .build();
        }

        if (isTextWithImage(update)) {
            Message message = update.getMessage();
            List<PhotoSize> photos = message.getPhoto();
            User user = message.getFrom();
            Integer messageId = message.getMessageId();

            String text = isNotBlank(message.getCaption()) ? message.getCaption() : message.getText();

            return ProcessableMessage.builder()
                    .processable(isFromUser(user) && isNotBlank(text))
                    .username(user.getUserName())
                    .userId(user.getId())
                    .chatId(message.getChatId())
                    .text(message.getCaption())
                    .messageId(messageId)
                    .hasPhoto(true)
                    .photos(photos)
                    .build();
        }

        return ProcessableMessage.builder()
                .processable(false)
                .build();
    }

    private boolean hasPhoto(CallbackQuery callbackQuery) {
        if (Message.class.isAssignableFrom(callbackQuery.getMessage().getClass())) {
            List<PhotoSize> photo = ((Message) callbackQuery.getMessage()).getPhoto();
            return CollectionUtils.isNotEmpty(photo);
        }
        return false;
    }

    private boolean hasPhoto(Message message) {
        return CollectionUtils.isNotEmpty(message.getPhoto());
    }

    /*private Long getUserId(Update update) {
        if (isText(update)) return update.getMessage().getFrom().getId();
        if (isCallbackData(update)) return update.getCallbackQuery().getFrom().getId();
        if (isPoll(update)) throw new UnsupportedOperationException("Don't know how to get user id from Poll");
        if (isPollAnswer(update)) return update.getPollAnswer().getUser().getId();
        if (isEditedMessage(update)) return update.getEditedMessage().getFrom().getId();
        log.warn("Unsupported type of update: [{}]", update);
        return null;
    }*/

    private boolean isText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }
    private boolean isTextWithImage(Update update) {return update.hasMessage() && hasPhoto(update.getMessage());}
    private boolean isCallbackData(Update update) {
        return update.hasCallbackQuery();
    }
    private boolean isPoll(Update update) {
        return update.hasPoll();
    }
    private boolean isPollAnswer(Update update) {
        return update.hasPollAnswer();
    }
    private boolean isEditedMessage(Update update) {
        return update.hasEditedMessage();
    }
    private boolean isFromUser(User user) {
        return BooleanUtils.isFalse(user.getIsBot());
    }

}
