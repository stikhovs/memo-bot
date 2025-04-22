package com.sergio.memo_bot.command_handler.card_set.import_card_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.COPY_HERE_CARD_SET_CONTENT_FROM_QUIZLET;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportCardsRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_CARDS_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        chatAwaitsInputService.clearAndSave(chatId, CommandType.IMPORT_CARDS_RESPONSE);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(COPY_HERE_CARD_SET_CONTENT_FROM_QUIZLET)
                .build();
    }
}