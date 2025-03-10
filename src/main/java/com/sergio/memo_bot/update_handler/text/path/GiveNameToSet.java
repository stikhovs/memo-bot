package com.sergio.memo_bot.update_handler.text.path;

import com.sergio.memo_bot.configuration.BotStateHandler;
import com.sergio.memo_bot.configuration.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiveNameToSet implements TextPath {
    public static final String CREATE_SET_URL = "/set/save";

    private final BotStateHandler stateHandler;

    private final RestTemplate restTemplate;

    @Override
    public boolean canProcess(Message message) {
        return stateHandler.getUserState(message.getFrom().getId()) == State.SET_NAMING;
    }

    @Override
    public BotApiMethodMessage process(Message message, Long chatId) {
        stateHandler.clearUserState(message.getFrom().getId());
        return SendMessage.builder().text("Дано название: %s".formatted(message.getText())).chatId(chatId).build();
    }
}
