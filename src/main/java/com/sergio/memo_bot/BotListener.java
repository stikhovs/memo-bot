package com.sergio.memo_bot;

import com.sergio.memo_bot.configuration.BotProperties;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserStateHolder;
import com.sergio.memo_bot.update_handler.BaseProcessor;
import com.sergio.memo_bot.util.UpdateMapper;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
public class BotListener extends TelegramLongPollingBot {
    private final List<BaseProcessor> handlers;
    protected final UpdateMapper updateMapper;
    private final UserStateHolder stateHolder;

    public BotListener(BotProperties botProperties, List<BaseProcessor> handlers, UpdateMapper updateMapper, UserStateHolder stateHolder) {
        super(botProperties.getApiKey());
        this.handlers = handlers;
        this.updateMapper = updateMapper;
        this.stateHolder = stateHolder;
    }

    @Override
    public void onUpdateReceived(Update update) {
        ProcessableMessage processableMessage = updateMapper.map(update);
        stateHolder.calculateNextState(processableMessage);
        handleAndReply(processableMessage);
    }

    public void handleAndReply(ProcessableMessage processableMessage) {
        handlers.stream()
                .filter(it -> it.canHandleByUserState(processableMessage))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find the right handler"))
                .replyTo(processableMessage, reply -> {
                    try {
                        execute(reply);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public String getBotUsername() {
        return "memmmo_test_bot";
    }



    public void sendText(Long who, String visibleText, String hiddenText){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(visibleText + ": " +"||" + hiddenText + "||")  // Wrap text with "||" for spoiler effect
                .parseMode("MarkdownV2")
                .build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }


}
