package com.sergio.memo_bot;

import com.sergio.memo_bot.configuration.BotProperties;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.service.SenderService;
import com.sergio.memo_bot.service.UpdateService;
import com.sergio.memo_bot.state.UserStateHolder;
import com.sergio.memo_bot.update_handler.BaseProcessor;
import com.sergio.memo_bot.util.UpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoBotConsumer implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    //    private final TelegramClient memoBotClient;
    private final BotProperties botProperties;
    private final List<BaseProcessor> handlers;
    protected final UpdateMapper updateMapper;
    private final UserStateHolder stateHolder;
    private final SenderService senderService;
    private final UpdateService updateService;

    @Override
    public void consume(Update update) {
        updateService.process(update);
        /*sender.send(SendMessage.builder()
                        .chatId(update.getMessage().getFrom().getId())
                        .text("Hello")
                .build());
        sender.send(SendMessage.builder()
                        .chatId(update.getMessage().getFrom().getId())
                        .text("How are you?")
                .build());*/
    }

    /*@Override
    public void consume(Update update) {
        ProcessableMessage processableMessage = updateMapper.map(update);
        stateHolder.calculateNextState(processableMessage);
        handleAndReply(processableMessage);
    }*/

   /* public void handleAndReply(ProcessableMessage processableMessage) {
        handlers.stream()
                .filter(it -> it.canHandleByUserState(processableMessage))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find the right handler"))
                .replyTo(processableMessage, senderService::send);
    }*/

    @Override
    public String getBotToken() {
        return botProperties.getApiKey();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    /*@Override
    public void onUpdateReceived(Update update) {
        ProcessableMessage processableMessage = updateMapper.map(update);
        stateHolder.calculateNextState(processableMessage);
        handleAndReply(processableMessage);
    }*/
/*
    @Override
    public String getBotUsername() {
        return "memmmo_test_bot";
    }*/



    /*public void sendText(Long who, String visibleText, String hiddenText){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(visibleText + ": " +"||" + hiddenText + "||")  // Wrap text with "||" for spoiler effect
                .parseMode("MarkdownV2")
                .build();    //Message content
        try {
            memoBotClient.execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }*/


}
