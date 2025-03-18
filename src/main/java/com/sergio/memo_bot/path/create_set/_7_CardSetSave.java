package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserCardSetState;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserStateHolder;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.UpdateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.sergio.memo_bot.state.UserStateType.CARD_SET_SAVE;

@Slf4j
@Component
@RequiredArgsConstructor
public class _7_CardSetSave extends AbstractProcessable {
    public static final String CREATE_SET_URL = "/set/save";

    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;

    private final RestTemplate restTemplate;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return CARD_SET_SAVE == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        CardSetDto cardSetDto = userCardSetState.getUserCardSet(userId).orElseThrow();
        userMessageHolder.clear(userId);
        log.info("Сохраняем: {}", cardSetDto);
        ResponseEntity<CardSetDto> response = callCreateSetApi(cardSetDto);
        if (response.getStatusCode().is2xxSuccessful()) {
            userCardSetState.clearUserCardSet(userId);
            return BotReply.builder()
                    .type(BotReplyType.EDIT_MESSAGE_TEXT)
                    .messageId(processableMessage.getMessageId())
                    .text("Набор карточек успешно сохранен!")
                    .chatId(processableMessage.getChatId())
                    .build();
        }
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Что-то пошло не так")
                .chatId(processableMessage.getChatId())
                .build();
    }

    private ResponseEntity<CardSetDto> callCreateSetApi(CardSetDto cardSetDto) {
        return restTemplate.postForEntity(
                CREATE_SET_URL,
                cardSetDto,
                CardSetDto.class);
    }
}
