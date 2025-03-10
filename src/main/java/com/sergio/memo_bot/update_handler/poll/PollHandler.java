package com.sergio.memo_bot.update_handler.poll;

import com.sergio.memo_bot.update_handler.UpdateHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;

@Service
public class PollHandler implements UpdateHandler {
    @Override
    public boolean canHandle(Update update) {
        return update.hasPoll();
    }

    @Override
    public BotApiMethodMessage handle(Update update) {
        System.out.println("Has poll");
        Poll poll = update.getPoll();
        PollOption answer = poll.getOptions()
                .stream()
                .filter(it -> it.getVoterCount() == 1)
                .findFirst()
                .orElseThrow();
        System.out.println("answer: " + answer);
        return SendMessage.builder().text("hello").build();
    }
}
