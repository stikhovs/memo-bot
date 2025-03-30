package com.sergio.memo_bot.service;

import com.sergio.memo_bot.Sender;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.AwaitsUserInput;
import com.sergio.memo_bot.persistence.repository.ChatAwaitsInputRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateService {
    private final List<CommandHandler> commandHandlers;
    private final UpdateMapper updateMapper;
    private final Sender sender;
    private final ChatAwaitsInputRepository chatAwaitsInputRepository;


    public void process(Update update) {
        ProcessableMessage processableMessage = updateMapper.map(update);

        if(CommandType.isCommandType(processableMessage.getText())) {
            Reply reply = handleCommand(CommandType.getByCommandText(processableMessage.getText()), processableMessage);
            send(reply);
        } else {
            List<AwaitsUserInput> ifAwaitsUserTextInput = chatAwaitsInputRepository.findIfAwaitsUserTextInput(processableMessage.getChatId());
            if (isNotEmpty(ifAwaitsUserTextInput)) {
                CommandType commandType = CommandType.getByCommandText(ifAwaitsUserTextInput.getFirst().getNextCommand());
                Reply reply = handleCommand(commandType, processableMessage);
                send(reply);
            }
        }

    }

    private void send(Reply reply) {
        if (reply instanceof BotReply botReply) {
            sender.send(BotReplyMapper.toBotApiMethod(botReply));
            Optional.ofNullable(botReply.getNextReply()).ifPresent(this::send);
        } else if (reply instanceof BotPartReply botPartReply) {
            ProcessableMessage processableMessage = botPartReply.getPreviousProcessableMessage()
                    .toBuilder()
                    .fromPartReply(true)
                    .text(botPartReply.getText())
                    .build();
            send(handleCommand(botPartReply.getNextCommand(), processableMessage));
        } else if (reply instanceof MultipleBotReply multipleBotReply) {
            sender.send(BotReplyMapper.toBotApiMethod(multipleBotReply));
            send(handleCommand(multipleBotReply.getNextCommand(), multipleBotReply.getPreviousProcessableMessage()));
        }
    }

    private Reply handleCommand(CommandType commandType, ProcessableMessage processableMessage) {
        CommandHandler commandHandler = getCommandHandler(commandType);
        return commandHandler.getReply(processableMessage);
    }

    private CommandHandler getCommandHandler(CommandType commandType) {
        return commandHandlers.stream()
                .filter(commandHandler -> commandHandler.canHandle(commandType))
                .findFirst()
                .orElseThrow();
    }


}
