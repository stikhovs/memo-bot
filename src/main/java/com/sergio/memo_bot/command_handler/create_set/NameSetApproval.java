package com.sergio.memo_bot.command_handler.create_set;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.repository.ChatAwaitsInputRepository;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NameSetApproval implements CommandHandler {

//    private final ObjectMapper objectMapper;
    private final ChatTempDataRepository chatTempDataRepository;
    private final ChatAwaitsInputRepository chatAwaitsInputRepository;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.NAME_SET == commandType;
    }

    @SneakyThrows
    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {

        chatAwaitsInputRepository.deleteByChatId(processableMessage.getChatId());

        CardSetDto cardSet = CardSetDto.builder()
                .title(processableMessage.getText())
                .build();
        System.out.println(new Gson().toJson(cardSet));
        chatTempDataRepository.save(ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .data(new Gson().toJson(cardSet))
                .build());

        return BotPartReply.builder()
                .nextCommand(CommandType.ADD_CARD_REQUEST)
                .type(BotReplyType.MESSAGE)
                .previousProcessableMessage(processableMessage)
                .chatId(processableMessage.getChatId())
                .text(EmojiConverter.getEmoji("U+2705") + " Будет создан набор: \"%s\"".formatted(processableMessage.getText()))
                .build();
    }
}
