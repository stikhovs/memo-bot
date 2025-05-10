package com.sergio.memo_bot.command_handler.card_set.import_card_set.from_message;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
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
public class ImportCardSetFromMessageMenu implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_CARD_SET_FROM_MESSAGE_MENU == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(IMPORT_CARD_SET_FROM_MESSAGE)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(IMPORT_SET, CommandType.CHOOSE_CATEGORY_FOR_MESSAGE_IMPORT_REQUEST),
                        Pair.of(IMPORT_INSTRUCTION, CommandType.IMPORT_FROM_MESSAGE_README),
                        Pair.of(BACK, CommandType.IMPORT_CARD_SET_MENU)
                )))
                .build();
    }
}