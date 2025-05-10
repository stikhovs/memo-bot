package com.sergio.memo_bot.command_handler.card_set.export_card_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.EXPORT_INSTRUCTION;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportCardSetRequest implements CommandHandler {

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXPORT_CARD_SET_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(EXPORT_INSTRUCTION)
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.EXPORT_CARD_SET_RESPONSE)
                        .build())
                .build();
    }

}