package com.sergio.memo_bot.update_handler.poll_answer;

import com.sergio.memo_bot.update_handler.UpdateHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Service
public class PollAnswerHandler implements UpdateHandler {
    @Override
    public boolean canHandle(Update update) {
        return update.hasPollAnswer();
    }

    @Override
    public BotApiMethodMessage handle(Update update) {
        System.out.println("Has poll");
        PollAnswer answer = update.getPollAnswer();
        Long chatId = answer.getUser().getId();
        System.out.println("answer: " + answer);
        /*return SendMessage.builder().chatId(chatId).text("hello from PollAnswer")
                .replyMarkup(ReplyKeyboardMarkup.builder().keyboardRow(new KeyboardRow(List.of(
                        KeyboardButton.builder().text("hehe").build(),
                        KeyboardButton.builder().text("hoho").build()
                ))).build())
                .build();*/
        return SendMessage.builder().chatId(chatId).text("hello from PollAnswer")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(
                                InlineKeyboardButton.builder().text("hehe").callbackData("myCallback 1").build(),
                                InlineKeyboardButton.builder().text("hoho").callbackData("myCallback 2").build()
                        ))
                .build()).build();
    }
}
