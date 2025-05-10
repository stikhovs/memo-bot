package com.sergio.memo_bot.command_handler.card_set.import_card_set.quizlet;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.STRING_IS_TOO_LONG;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.length;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportCardSetTitleResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_CARD_SET_TITLE_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);

        if (isBlank(processableMessage.getText()) || length(processableMessage.getText()) > 100) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(STRING_IS_TOO_LONG)
                    .nextReply(NextReply.builder()
                            .previousProcessableMessage(processableMessage)
                            .nextCommand(CommandType.IMPORT_CARD_SET_TITLE_REQUEST)
                            .build())
                    .build();
        }

        chatTempDataService.clearAndSave(chatId,
                ChatTempData.builder()
                        .chatId(chatId)
                        .data(processableMessage.getText())
                        .command(CommandType.IMPORT_CARD_SET_TITLE_RESPONSE)
                        .build()
        );

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.IMPORT_CARDS_REQUEST)
                .build();
    }
}