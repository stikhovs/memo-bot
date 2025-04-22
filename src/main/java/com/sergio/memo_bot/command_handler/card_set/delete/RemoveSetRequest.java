package com.sergio.memo_bot.command_handler.card_set.delete;

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
public class RemoveSetRequest implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.REMOVE_SET_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(ARE_YOU_SURE_YOU_WANT_TO_DELETE_CARD_SET)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of(YES, CommandType.REMOVE_SET_RESPONSE),
                        Pair.of(NO, CommandType.GET_CARD_SET_INFO)
                )))
                .build();
    }
}
