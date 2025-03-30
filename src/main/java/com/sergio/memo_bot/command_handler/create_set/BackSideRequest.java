package com.sergio.memo_bot.command_handler.create_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.AwaitsUserInput;
import com.sergio.memo_bot.persistence.repository.ChatAwaitsInputRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class BackSideRequest implements CommandHandler {

    private final ChatAwaitsInputRepository chatAwaitsInputRepository;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.INSERT_BACK_SIDE == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {

        System.out.println(chatAwaitsInputRepository.findAll());

        AwaitsUserInput awaitsUserInput = chatAwaitsInputRepository.findOneByChatId(processableMessage.getChatId())
                .map(aui -> aui.toBuilder()
                        .inputType("TEXT")
                        .nextCommand(CommandType.BACK_SIDE_RECEIVED.getCommandText())
                        .chatId(processableMessage.getChatId())
                        .build())
                .orElseThrow(() -> new RuntimeException("Must find the record by chatId"));

        chatAwaitsInputRepository.save(awaitsUserInput);

        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Введите заднюю сторону карточки")
                .messageId(processableMessage.getMessageId())
                .chatId(processableMessage.getChatId())
//                .replyMarkup(MarkUpUtil.getForceReplyMarkup("Задняя сторона"))
                .build();
    }
}
