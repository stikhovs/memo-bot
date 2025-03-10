package com.sergio.memo_bot.update_handler.callback_data.path.create_set;

import com.sergio.memo_bot.configuration.BotStateHandler;
import com.sergio.memo_bot.configuration.State;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.UserDto;
import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.ReplyParameters;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateSet implements CallBackPath {
    private static final String CREATE_SET = "Create set";
    private final BotStateHandler stateHandler;

    @Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return StringUtils.equalsIgnoreCase(CREATE_SET, callbackQuery.getData());
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        stateHandler.setUserState(callbackQuery.getFrom().getId(), State.SET_NAMING);
        return SendMessage.builder()
                .text("Как будет называться ваш новый набор?")
                .chatId(chatId)
                .build();
    }

//    private void createSet() {
//        restTemplate.postForEntity(
//                CREATE_SET_URL,
//                CardSetDto.builder()
//                        .title()
//                        .userId()
//                        .cards()
//                        .build(),
//                CreateSet.class);
//    }
}
