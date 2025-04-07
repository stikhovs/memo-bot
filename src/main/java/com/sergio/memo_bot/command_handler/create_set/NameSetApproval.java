package com.sergio.memo_bot.command_handler.create_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.EmojiConverter;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NameSetApproval implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.NAME_SET == commandType;
    }

    @SneakyThrows
    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);

        /*CardSetDto cardSet = CardSetDto.builder()
                .title(processableMessage.getText())
                .build();
        System.out.println(new Gson().toJson(cardSet));*/

        chatTempDataService.clearAndSave(chatId,
                ChatTempData.builder()
                        .chatId(chatId)
                        .data(processableMessage.getText())
                        .command(CommandType.NAME_SET)
                        .build()
        );

        return BotPartReply.builder()
                .nextCommand(CommandType.ADD_CARD_REQUEST)
//                .type(BotReplyType.MESSAGE)
                .previousProcessableMessage(processableMessage)
                .chatId(chatId)
                .text(EmojiConverter.getEmoji("U+2705") + " Будет создан набор: \"%s\"".formatted(processableMessage.getText()))
                .build();
    }
}
