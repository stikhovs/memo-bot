package com.sergio.memo_bot.command_handler.exercise.answer_input;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputData;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputItem;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnswerInputRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.ANSWER_INPUT_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        AnswerInputData answerInputData = chatTempDataService.mapDataToType(chatId, CommandType.ANSWER_INPUT_REQUEST, AnswerInputData.class);

        int currentIndex = answerInputData.getCurrentIndex();

        if (currentIndex == answerInputData.getTotalNumberOfItems()) {

            if (answerInputData.isPageable()) {
                if (answerInputData.getCurrentPage() + 1 == answerInputData.getTotalNumberOfPages()) {
                    // if it was the last page
                    chatTempDataService.clear(chatId, CommandType.ANSWER_INPUT_REQUEST);
                    return BotMessageReply.builder()
                            .chatId(chatId)
                            .text(EXERCISE_FINISHED)
                            .nextReply(NextReply.builder()
                                    .previousProcessableMessage(processableMessage)
                                    .nextCommand(CommandType.EXERCISES_RESPONSE)
                                    .build())
                            .build();
                } else {
                    return BotMessageReply.builder()
                            .chatId(chatId)
                            .text(LEVEL_FINISHED.formatted(answerInputData.getCurrentPage() + 1, answerInputData.getTotalNumberOfPages()))
                            .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                    org.apache.commons.lang3.tuple.Pair.of(LEAVE_LEVEL, CommandType.EXERCISES_RESPONSE),
                                    org.apache.commons.lang3.tuple.Pair.of(NEXT_LEVEL, CommandType.ANSWER_INPUT_PREPARE)
                            )))
                            .build();
                }
            } else {
                return BotMessageReply.builder()
                        .chatId(chatId)
                        .text(EXERCISE_FINISHED)
                        .nextReply(NextReply.builder()
                                .previousProcessableMessage(processableMessage)
                                .nextCommand(CommandType.EXERCISES_RESPONSE)
                                .build())
                        .build();
            }
        } else {
            AnswerInputItem answerInputItem = answerInputData.getAnswerInputItems().get(currentIndex);
            chatAwaitsInputService.clearAndSave(chatId, CommandType.ANSWER_INPUT_RESPONSE);

            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(INSERT_ANSWER_FOR.formatted(answerInputItem.getQuestion()))
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                            Pair.of(BACK, CommandType.EXERCISES_RESPONSE)
                    )))
                    .build();
        }
    }
}
