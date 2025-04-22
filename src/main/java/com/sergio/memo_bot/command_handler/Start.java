package com.sergio.memo_bot.command_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.dto.UserDto;
import com.sergio.memo_bot.external.ApiCallService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.reply_text.ReplyTextConstant;
import com.sergio.memo_bot.state.CommandType;
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
                    .nextCommand(CommandType.MAIN_MENU)
                    .build();
        }
        return registerUser(processableMessage);
    }

    public Reply registerUser(ProcessableMessage processableMessage) {
        callCreateUserApi(processableMessage);
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(ReplyTextConstant.START)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.MAIN_MENU)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .chatId(processableMessage.getChatId())
                .build();
    }

    private UserDto callCreateUserApi(ProcessableMessage processableMessage) {
        return apiCallService.saveUser(UserDto.builder()
                .username(processableMessage.getUsername())
                .telegramUserId(processableMessage.getUserId())
                .telegramChatId(processableMessage.getChatId())
                .build());
    }

}
