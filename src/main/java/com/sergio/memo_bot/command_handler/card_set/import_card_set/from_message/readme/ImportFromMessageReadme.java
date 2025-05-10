package com.sergio.memo_bot.command_handler.card_set.import_card_set.from_message.readme;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.Reply;
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
public class ImportFromMessageReadme implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_FROM_MESSAGE_README == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        return BotMessageReply.builder()
                .chatId(chatId)
                .text(IMPORT_CARD_SET_FROM_MESSAGE_README)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(BACK, CommandType.IMPORT_CARD_SET_FROM_MESSAGE_MENU),
                        Pair.of(IMPORT_SET, CommandType.INSERT_IMPORT_MESSAGE_REQUEST)
                )))
                .build();
    }
}