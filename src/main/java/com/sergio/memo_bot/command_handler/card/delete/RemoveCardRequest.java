package com.sergio.memo_bot.command_handler.card.delete;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveCardRequest implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.REMOVE_CARD_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
//                .type(BotReplyType.EDIT_MESSAGE_TEXT)
//                .messageId(processableMessage.getMessageId())
                .text("Вы уверены, что хотите удалить карточку?")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of("Да", CommandType.REMOVE_CARD_RESPONSE),
                        Pair.of("Нет", CommandType.EDIT_CARD_REQUEST)
                )))
                .build();
    }
}
