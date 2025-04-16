package com.sergio.memo_bot.service;

import com.sergio.memo_bot.persistence.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderService {
    private final TelegramClient memoBotClient;
    private final ChatMessageService chatMessageService;

    @Transactional
    public void send(ReplyData reply) {
        try {
            Serializable execute = memoBotClient.execute(reply.getReply());
            saveOrUpdateIfPossible(execute, reply);
        } catch (TelegramApiException e) {
            String failedReplyMethodName = reply.getReply().getMethod();
            if (failedReplyMethodName.equals("editmessagetext") || failedReplyMethodName.equals("editmessagereplymarkup")) {
                log.warn("Couldn't edit message. Reason: [{}]. This message will be deleted from the database", e.getMessage());
                chatMessageService.delete(reply.getChatId(), List.of(reply.getMessageId()));
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveOrUpdateIfPossible(Serializable sentMessage, ReplyData reply) {
        switch (reply.getReply().getMethod()) {
            case "sendmessage" ->
                    chatMessageService.saveFromBot(((Message) sentMessage).getChatId(), ((Message) sentMessage).getMessageId(), reply.isHasButtons());
            case "editmessagetext", "editmessagereplymarkup" ->
                    chatMessageService.saveFromBot(reply.getChatId(), reply.getMessageId(), reply.isHasButtons());
        }
    }

}
