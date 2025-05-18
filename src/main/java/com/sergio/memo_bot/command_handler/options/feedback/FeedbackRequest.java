package com.sergio.memo_bot.command_handler.options.feedback;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.BACK;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.INSERT_YOUR_FEEDBACK;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackRequest implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.FEEDBACK_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clearAndSave(chatId, CommandType.FEEDBACK_RESPONSE);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(INSERT_YOUR_FEEDBACK)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of(BACK, CommandType.BOT_OPTIONS)
                )))
                .build();
    }
}