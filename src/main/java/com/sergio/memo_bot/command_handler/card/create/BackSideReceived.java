package com.sergio.memo_bot.command_handler.card.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CARD_WAS_ADDED;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.STRING_IS_TOO_LONG;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;


@Slf4j
@Component
@RequiredArgsConstructor
public class BackSideReceived implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.BACK_SIDE_RECEIVED == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);

        if (isBlank(processableMessage.getText()) || length(processableMessage.getText()) > 100) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(STRING_IS_TOO_LONG)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.INSERT_BACK_SIDE)
                            .build())
                    .build();
        }

        ChatTempData backSide = chatTempDataService.clearAndSave(chatId,
                ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.BACK_SIDE_RECEIVED)
                        .data(processableMessage.getText())
                        .build()
        );
        ChatTempData frontSide = chatTempDataService.get(chatId, CommandType.FRONT_SIDE_RECEIVED);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CARD_WAS_ADDED.formatted(frontSide.getData(), backSide.getData()))
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.ADD_CARD_RESPONSE)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();
    }

}
