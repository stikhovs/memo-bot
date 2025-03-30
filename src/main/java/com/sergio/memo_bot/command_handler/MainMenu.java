package com.sergio.memo_bot.command_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.repository.ChatAwaitsInputRepository;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainMenu implements CommandHandler {

    private final ChatAwaitsInputRepository chatAwaitsInputRepository;
    private final ChatTempDataRepository chatTempDataRepository;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.MAIN_MENU == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        chatAwaitsInputRepository.deleteByChatId(processableMessage.getChatId());
        chatTempDataRepository.deleteByChatId(processableMessage.getChatId());

        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Выберите действие")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Создать набор", CommandType.CREATE_SET),
                        Pair.of("Просмотреть наборы", CommandType.GET_ALL_SETS)
                )))
                .chatId(processableMessage.getChatId())
                .build();
    }
}
