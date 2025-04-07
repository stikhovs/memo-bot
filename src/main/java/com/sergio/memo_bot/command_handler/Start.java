package com.sergio.memo_bot.command_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.dto.UserDto;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Start implements CommandHandler {

    private final ApiCallService apiCallService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.START == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        return getOrCreateUser(processableMessage);
    }

    private Reply getOrCreateUser(ProcessableMessage processableMessage) {
        Optional<UserDto> user = apiCallService.getUser(processableMessage.getChatId());
        if (user.isPresent()) {
            return BotPartReply.builder()
                    .previousProcessableMessage(processableMessage)
                    .chatId(processableMessage.getChatId())
//                    .messageId(processableMessage.getMessageId())
                    .nextCommand(CommandType.MAIN_MENU)
                    .build();
        }
        return registerUser(processableMessage);
    }

    public Reply registerUser(ProcessableMessage processableMessage) {
        callCreateUserApi(processableMessage);
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Добро пожаловать!")
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.MAIN_MENU)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .chatId(processableMessage.getChatId())
//                .messageId(processableMessage.getMessageId())
                .build();
    }

    /*private InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return MarkUpUtil.getInlineKeyboardMarkup(List.of(
                Pair.of("Создать набор", CommandType.CREATE_SET),
                Pair.of("Импортировать набор", CommandType.IMPORT_SET)
        ));
    }*/

    private UserDto callCreateUserApi(ProcessableMessage processableMessage) {
        return apiCallService.saveUser(UserDto.builder()
                .username(processableMessage.getUsername())
                .telegramUserId(processableMessage.getUserId())
                .telegramChatId(processableMessage.getChatId())
                .build());
    }

}
