package com.sergio.memo_bot.service;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.mapper.ReplyMapper;
import com.sergio.memo_bot.persistence.entity.AwaitsUserInput;
import com.sergio.memo_bot.persistence.entity.MessageContentType;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatMessageService;
import com.sergio.memo_bot.reply.*;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.UpdateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.SOMETHING_WENT_WRONG;
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


    @Transactional
    public void process(Update update) {
        ProcessableMessage processableMessage = updateMapper.map(update);
        try {
            if (processableMessage.isProcessable()) {
                chatMessageService.saveFromUser(processableMessage.getChatId(), processableMessage.getMessageId(), processableMessage.isHasPhoto() ? MessageContentType.IMAGE : MessageContentType.TEXT);
                if (CommandType.isCommandType(processableMessage.getText())) {
                    log.info("Consumed command: {}", processableMessage);
                    Reply reply = handleCommand(CommandType.getByCommandText(processableMessage.getText()), processableMessage);
                    send(reply);
                } else {
                    List<AwaitsUserInput> ifAwaitsUserTextInput = chatAwaitsInputService.findAll(processableMessage.getChatId());
                    if (isNotEmpty(ifAwaitsUserTextInput)) {
                        log.info("Consumed user input: {}", processableMessage);
                        CommandType commandType = ifAwaitsUserTextInput.getFirst().getNextCommand();
                        Reply reply = handleCommand(commandType, processableMessage);
                        send(reply);
                    }
                }
            }
        } catch (Throwable ex) {
            log.error("Something went wrong", ex);
            senderService.send(ReplyData.builder()
                            .chatId(processableMessage.getChatId())
                            .reply(SendMessage.builder()
                                    .chatId(processableMessage.getChatId())
                                    .text(SOMETHING_WENT_WRONG)
                                    .build())
                    .build());
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
        } else if (reply instanceof DeleteMessageReply deleteMessageReply) {
            senderService.send(replyMapper.toReplyData(deleteMessageReply));
            chatMessageService.delete(deleteMessageReply.getChatId(), deleteMessageReply.getMessageIds());
            NextReply nextReply = deleteMessageReply.getNextReply();
            if (nextReply != null) {
                send(handleCommand(nextReply.getNextCommand(), nextReply.getPreviousProcessableMessage()));
            }
        } else if (reply instanceof BotQuizReply quizReply) {
            for (ReplyData replyData : replyMapper.toReplyData(quizReply)) {
                senderService.send(replyData);
            }
        } else if (reply instanceof BotImageReply imageReply) {
            senderService.send(replyMapper.toReplyData(imageReply));
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
