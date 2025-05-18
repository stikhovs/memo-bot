package com.sergio.memo_bot.command_handler.options;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotOptions implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.BOT_OPTIONS == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOOSE_BOT_OPTION)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                                Pair.of(BOT_INFO, CommandType.INFO),
                                Pair.of(BOT_FEEDBACK_OPTION, CommandType.FEEDBACK_REQUEST),
                                Pair.of(BACK, CommandType.MAIN_MENU))
                ))
                .build();
    }
}