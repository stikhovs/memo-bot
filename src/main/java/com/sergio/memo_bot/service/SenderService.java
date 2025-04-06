package com.sergio.memo_bot.service;

import com.sergio.memo_bot.persistence.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

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
            throw new RuntimeException(e);
        }
    }

    private void saveOrUpdateIfPossible(Serializable sentMessage, ReplyData reply) {
        switch (reply.getReply().getMethod()) {
            case "sendmessage" -> chatMessageService.saveFromBot(((Message)sentMessage).getChatId(), ((Message)sentMessage).getMessageId(), reply.isHasButtons());
            case "editmessagetext", "editmessagereplymarkup"  -> chatMessageService.saveFromBot(reply.getChatId(), reply.getMessageId(), reply.isHasButtons());
        }
    }

}
