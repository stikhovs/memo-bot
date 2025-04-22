package com.sergio.memo_bot.command_handler.card_set.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CARD_SET_SUCCESSFULLY_SAVED;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveSetResponse implements CommandHandler {

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SAVE_CARD_SET_RESPONSE == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(CARD_SET_SUCCESSFULLY_SAVED)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.MAIN_MENU)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();
    }

}
