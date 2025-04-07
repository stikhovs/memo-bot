package com.sergio.memo_bot.service;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.mapper.ReplyMapper;
import com.sergio.memo_bot.persistence.entity.AwaitsUserInput;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatMessageService;
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
    private final SenderService senderService;
    private final ChatAwaitsInputService chatAwaitsInputService;
    private final ChatMessageService chatMessageService;
    private final ReplyMapper replyMapper;


    public void process(Update update) {
        ProcessableMessage processableMessage = updateMapper.map(update);
        chatMessageService.saveFromUser(processableMessage.getChatId(), processableMessage.getMessageId());

        if (processableMessage.isProcessable()) {
            if (CommandType.isCommandType(processableMessage.getText())) {
                log.info("Consumed command: {}", processableMessage);
                Reply reply = handleCommand(CommandType.getByCommandText(processableMessage.getText()), processableMessage);
                send(reply);
            } else {
                List<AwaitsUserInput> ifAwaitsUserTextInput = chatAwaitsInputService.findAll(processableMessage.getChatId());
                if (isNotEmpty(ifAwaitsUserTextInput)) {
                    log.info("Consumed user input: {}", processableMessage);
                    /*Integer newMessageId = chatMessageService.findMessageId(processableMessage.getChatId()).orElse(null);
                    log.info("Changing messageId from {} to {}", processableMessage.getMessageId(), newMessageId);*/
                    CommandType commandType = ifAwaitsUserTextInput.getFirst().getNextCommand();
                    Reply reply = handleCommand(commandType, processableMessage);
                    send(reply);
                }
            }
        }

    }

    private void send(Reply reply) {

        if (reply instanceof BotMessageReply messageReply) {
            for (ReplyData replyData : replyMapper.toReplyData(messageReply)) {
                senderService.send(replyData);
            }
            NextReply nextReply = messageReply.getNextReply();
            if (nextReply != null) {
                send(handleCommand(nextReply.getNextCommand(), nextReply.getPreviousProcessableMessage()));
            }
        } else if (reply instanceof BotPartReply botPartReply) {
            ProcessableMessage processableMessage = botPartReply.getPreviousProcessableMessage()
                    .toBuilder()
                    .fromPartReply(true)
                    .text(botPartReply.getText())
                    .build();
            send(handleCommand(botPartReply.getNextCommand(), processableMessage));
        } /*else if (reply instanceof MultipleBotReply multipleBotReply) {
            senderService.send(replyMapper.toReplyData(multipleBotReply));
            send(handleCommand(multipleBotReply.getNextCommand(), multipleBotReply.getPreviousProcessableMessage()));
        }*/ else if (reply instanceof DeleteMessageReply deleteMessageReply) {
            senderService.send(replyMapper.toReplyData(deleteMessageReply));
            chatMessageService.delete(deleteMessageReply.getChatId(), deleteMessageReply.getMessageIds());
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
