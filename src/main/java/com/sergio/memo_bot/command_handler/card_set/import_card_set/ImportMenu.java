package com.sergio.memo_bot.command_handler.card_set.import_card_set;

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
public class ImportMenu implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_CARD_SET_MENU == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOOSE_YOUR_IMPORT)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(IMPORT_FROM_MESSAGE, CommandType.IMPORT_CARD_SET_FROM_MESSAGE_MENU),
                        Pair.of(IMPORT_FROM_QUIZLET, CommandType.IMPORT_CARD_SET_FROM_QUIZLET_MENU),
                        Pair.of(BACK, CommandType.CARD_SET_MENU)
                )))
                .build();
    }
}