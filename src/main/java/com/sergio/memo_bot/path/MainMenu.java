package com.sergio.memo_bot.path;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.*;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MainMenu extends AbstractProcessable {

    private final UserStateHolder userStateHolder;
    private final UserMessageHolder userMessageHolder;
    private final UserInputState userInputState;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return userStateType == UserStateType.MAIN_MENU;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        userStateHolder.clear(userId);
        userMessageHolder.clear(userId);
        userInputState.clear(userId);
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Выберите действие")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Создать набор", CommandType.CREATE_SET),
                        Pair.of("Просмотреть наборы", CommandType.GET_ALL_SETS)
                )))
                .chatId(processableMessage.getChatId())
                .build();
    }
}
