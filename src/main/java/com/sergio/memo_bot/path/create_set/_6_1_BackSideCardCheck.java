package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserStateHolder;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.sergio.memo_bot.state.UserStateType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class _6_1_BackSideCardCheck extends AbstractProcessable {
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return BACK_SIDE_CARD_CHECK == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        userMessageHolder.setUserMessage(processableMessage.getUserId(), processableMessage.getText());
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Обратная сторона: %s\nВерно?".formatted(processableMessage.getText()))
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+2705") + " Да", CommandType.ACCEPT_BACK_SIDE),
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " Нет", CommandType.DECLINE_BACK_SIDE)
                        ))
                )
                .chatId(processableMessage.getChatId())
                .build();
    }

}
