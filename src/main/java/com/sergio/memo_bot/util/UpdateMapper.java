package com.sergio.memo_bot.util;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserStateHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;


@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateMapper {

    private final UserStateHolder userStateHolder;
//    private final UserChatIdHolder userChatIdHolder;

    public ProcessableMessage map(Update update) {
        if (isText(update)) {
            Message message = update.getMessage();
            User user = message.getFrom();
            Integer messageId = message.getMessageId();
            return ProcessableMessage.builder()
                    .username(user.getUserName())
                    .userId(user.getId())
                    .chatId(message.getChatId())
                    .text(message.getText())
                    .messageId(messageId)
                    .build();
        }

        if (isCallbackData(update)) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            User user = callbackQuery.getFrom();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            return ProcessableMessage.builder()
                    .username(user.getUserName())
                    .userId(user.getId())
                    .chatId(callbackQuery.getMessage().getChatId())
                    .text(callbackQuery.getData())
                    .messageId(messageId)
                    .build();
        }

        if (isEditedMessage(update)) {
            Message message = update.getEditedMessage();
            User user = message.getFrom();
            Integer messageId = message.getMessageId();
            return ProcessableMessage.builder()
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
//                    .username(user.getUserName())
//                    .userId(user.getId())
//                    .chatId(message.getChatId())
                    .text(answer.getText())
                    .build();
        }

        if (isPollAnswer(update)) {
            PollAnswer pollAnswer = update.getPollAnswer();
            User user = pollAnswer.getUser();
            return ProcessableMessage.builder()
                    .username(user.getUserName())
                    .userId(user.getId())
//                    .chatId(pollAnswer.getChatId())
//                    .text(pollAnswer.getOptionIds())
                    .build();
        }

        return ProcessableMessage.builder()
//                .userId(update.get)
                .build();
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

}
