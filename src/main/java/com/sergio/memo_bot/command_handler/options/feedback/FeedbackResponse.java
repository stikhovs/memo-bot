package com.sergio.memo_bot.command_handler.options.feedback;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.service.FeedbackSenderService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.YOUR_FEEDBACK_WAS_SENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackResponse implements CommandHandler {

    private final FeedbackSenderService feedbackSenderService;
    private final ChatAwaitsInputService chatAwaitsInputService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.FEEDBACK_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);

        feedbackSenderService.sendFeedbackToAdminChat(processableMessage);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(processableMessage.getText() + YOUR_FEEDBACK_WAS_SENT)
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.MAIN_MENU)
                        .build())
                .build();
    }
}