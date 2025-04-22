package com.sergio.memo_bot.command_handler.exercise.answer_input;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputData;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputItem;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.EXERCISE_FINISHED;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.INSERT_ANSWER_FOR;

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
        AnswerInputData answerInputData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.ANSWER_INPUT_REQUEST, AnswerInputData.class);

        int currentIndex = answerInputData.getCurrentIndex();

        if (currentIndex == answerInputData.getTotalNumberOfItems()) {

            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text(EXERCISE_FINISHED)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                            .build())
                    .build();
        } else {
            AnswerInputItem answerInputItem = answerInputData.getAnswerInputItems().get(currentIndex);
            chatAwaitsInputService.clearAndSave(processableMessage.getChatId(), CommandType.ANSWER_INPUT_RESPONSE);

            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text(INSERT_ANSWER_FOR.formatted(answerInputItem.getQuestion()))
                    .parseMode(ParseMode.HTML)
                    .build();
        }
    }
}
