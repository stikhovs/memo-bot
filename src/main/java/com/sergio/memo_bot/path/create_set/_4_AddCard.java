package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserInputState;
import com.sergio.memo_bot.state.UserStateHolder;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.UpdateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.state.UserStateType.ADD_CARD_REQUEST;

@Slf4j
@Component
@RequiredArgsConstructor
public class _4_AddCard extends AbstractProcessable {

    private final UserInputState userInputState;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return ADD_CARD_REQUEST == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        userInputState.setUserInputState(processableMessage.getUserId(), true);
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Передняя сторона карточки:")
                .chatId(processableMessage.getChatId())
                .build();
    }

    /*@Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return PathState.CARD_CREATION_COMPLETED == userPathState.getUserState(callbackQuery.getFrom().getId())
                && callbackQuery.getData().equals("Add one more card");
    }

    @Override
    public boolean canProcess(Message message) {
        return PathState.CARD_CREATION_BEGIN == userPathState.getUserState(message.getFrom().getId())
                && message.getText().equals("Прекрасно! Теперь давайте добавим карточки");
    }*/

    /*@Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        Long userId = callbackQuery.getFrom().getId();
        return sendMessage(userId, chatId);
    }

    @Override
    public BotApiMethodMessage process(Message message, Long chatId) {
        Long userId = message.getFrom().getId();
        return sendMessage(userId, chatId);
    }

    private SendMessage sendMessage(Long userId, Long chatId) {
        userPathState.setUserState(userId, PathState.CARD_CREATION_FRONT_SIDE);
        return SendMessage.builder()
                .text("Передняя сторона карточки:")
                .chatId(chatId)
                .build();
    }*/
}
