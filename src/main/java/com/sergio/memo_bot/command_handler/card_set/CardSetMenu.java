package com.sergio.memo_bot.command_handler.card_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardSetMenu implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CARD_SET_MENU == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(CARD_SETS)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(CREATE_SET, CommandType.SET_CREATION_START),
                        Pair.of(CHOOSE_SET, CommandType.CHOOSE_SET_REQUEST),
                        Pair.of(IMPORT_SET, CommandType.IMPORT_CARD_SET_MENU),
                        Pair.of(BACK, CommandType.MAIN_MENU)
                )))
                .build();
    }
}