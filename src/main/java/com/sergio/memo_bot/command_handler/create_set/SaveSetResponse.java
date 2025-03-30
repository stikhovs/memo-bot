package com.sergio.memo_bot.command_handler.create_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.MultipleBotReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
        return BotReply.builder()
                .type(BotReplyType.EDIT_MESSAGE_REPLY_MARKUP)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .nextReply(
                        MultipleBotReply.builder()
                                .type(BotReplyType.MESSAGE)
                                .messageId(processableMessage.getMessageId())
                                .chatId(processableMessage.getChatId())
                                .text("Набор карточек успешно сохранен!")
                                .nextCommand(CommandType.MAIN_MENU)
                                .previousProcessableMessage(processableMessage)
                                .build()
                )
                .build();

    }

}
