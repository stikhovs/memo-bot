package com.sergio.memo_bot.command_handler.exercise.answer_input;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputData;
import com.sergio.memo_bot.command_handler.exercise.answer_input.dto.AnswerInputItem;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
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

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CORRECT;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.INCORRECT;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnswerInputResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.ANSWER_INPUT_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        AnswerInputData answerInputData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.ANSWER_INPUT_REQUEST, AnswerInputData.class);

        int currentIndex = answerInputData.getCurrentIndex();
        AnswerInputItem answerInputItem = answerInputData.getAnswerInputItems().get(currentIndex);

        String text;
        if (answerInputItem.getCorrectAnswer().equalsIgnoreCase(processableMessage.getText())) {
            text = CORRECT;
        } else {
            text = INCORRECT.formatted(answerInputItem.getCorrectAnswer());
        }

        chatAwaitsInputService.clear(processableMessage.getChatId());
        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.ANSWER_INPUT_REQUEST)
                .data(new Gson().toJson(
                        answerInputData.toBuilder()
                                .currentIndex(currentIndex + 1)
                                .build()
                ))
                .build());


        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(text)
                .parseMode(ParseMode.HTML)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.ANSWER_INPUT_REQUEST)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();

    }
}
