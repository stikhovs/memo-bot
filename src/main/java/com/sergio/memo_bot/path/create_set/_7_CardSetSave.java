package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.state.PathState;
import com.sergio.memo_bot.state.UserCardSetState;
import com.sergio.memo_bot.state.UserPathState;
import com.sergio.memo_bot.update_handler.callback_data.path.CallBackPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Component
@RequiredArgsConstructor
public class _7_CardSetSave implements CallBackPath {
    public static final String CREATE_SET_URL = "/set/save";

    private final UserPathState userPathState;
    private final UserCardSetState userCardSetState;

    private final RestTemplate restTemplate;

    @Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getFrom().getId();
        return PathState.CARD_CREATION_COMPLETED == userPathState.getUserState(userId)
                && callbackQuery.getData().equals("Save cardSet");
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        Long userId = callbackQuery.getFrom().getId();
        CardSetDto cardSetDto = userCardSetState.getUserCardSet(userId).orElseThrow();
        ResponseEntity<CardSetDto> response = callCreateSetApi(cardSetDto);
        if (response.getStatusCode().is2xxSuccessful()) {
            userCardSetState.clearUserCardSet(userId);
            return SendMessage.builder().chatId(chatId).text("Набор карточек успешно сохранен!").build();
        }
        return SendMessage.builder().chatId(chatId).text("Что-то пошло не так").build();
    }

    private ResponseEntity<CardSetDto> callCreateSetApi(CardSetDto cardSetDto) {
        return restTemplate.postForEntity(
                CREATE_SET_URL,
                cardSetDto,
                CardSetDto.class);
    }
}
