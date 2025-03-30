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


@Slf4j
@Component
@RequiredArgsConstructor
public class FrontSideRequest implements CommandHandler {

    private final ChatAwaitsInputRepository chatAwaitsInputRepository;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.INSERT_FRONT_SIDE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        chatAwaitsInputRepository.save(AwaitsUserInput.builder()
                .chatId(processableMessage.getChatId())
                .inputType("TEXT")
                .nextCommand(CommandType.FRONT_SIDE_RECEIVED.getCommandText())
                .build());

        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Введите переднюю сторону карточки")
                .messageId(processableMessage.getMessageId())
                .chatId(processableMessage.getChatId())
//                .replyMarkup(MarkUpUtil.getForceReplyMarkup("Передняя сторона"))
                .build();
    }
}
