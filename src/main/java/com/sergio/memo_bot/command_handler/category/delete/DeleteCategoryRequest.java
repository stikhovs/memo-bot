package com.sergio.memo_bot.command_handler.category.delete;

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
public class DeleteCategoryRequest implements CommandHandler {

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.DELETE_CATEGORY_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(CHOOSE_OPTION_TO_DELETE)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(DELETE_CATEGORY_AND_KEEP_CARD_SETS, CommandType.DELETE_CATEGORY_WITHOUT_SETS_REQUEST),
                        Pair.of(DELETE_CATEGORY_AND_DELETE_CARD_SETS, CommandType.DELETE_CATEGORY_WITH_SETS_REQUEST),
                        Pair.of(BACK, CommandType.GET_CATEGORY_INFO_REQUEST)
                )))
                .build();
    }
}