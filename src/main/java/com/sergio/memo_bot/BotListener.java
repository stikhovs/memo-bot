package com.sergio.memo_bot;

import com.sergio.memo_bot.configuration.BotProperties;
import com.sergio.memo_bot.update_handler.UpdateHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Service
public class BotListener extends TelegramLongPollingBot {

    private final List<UpdateHandler> handlers;

    public BotListener(BotProperties botProperties, List<UpdateHandler> handlers) {
        super(botProperties.getApiKey());
        this.handlers = handlers;
    }

    @Override
    public void onUpdateReceived(Update update) {
        UpdateHandler handler = handlers.stream()
                .filter(updateHandler -> updateHandler.canHandle(update))
                .findFirst()
                .orElseThrow();

        BotApiMethodMessage nextMessage = handler.handle(update);

        try {
            execute(nextMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        /*if (update.hasMessage()) {
            var msg = update.getMessage();
            var user = msg.getFrom();
            sendText(user.getId(), "This is visible", "this is hidden");
            return;
        }
        if (update.hasPoll()) {
            System.out.println("Has poll");
            Poll poll = update.getPoll();
            PollOption answer = poll.getOptions()
                    .stream()
                    .filter(it -> it.getVoterCount() == 1)
                    .findFirst()
                    .orElseThrow();
            System.out.println("answer: " + answer);
            return;
        }
        if (update.hasPollAnswer()) {
            System.out.println("Has poll answer");
            PollAnswer pollAnswer = update.getPollAnswer();
            List<Integer> optionIds = pollAnswer.getOptionIds();
            System.out.println(optionIds);
            return;
        }*/

//        sendText(user.getId(), "Hello");
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
