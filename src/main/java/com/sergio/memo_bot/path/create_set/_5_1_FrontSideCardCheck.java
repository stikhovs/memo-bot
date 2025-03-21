package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.*;
import com.sergio.memo_bot.update_handler.BaseProcessor;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.state.UserStateType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class _5_1_FrontSideCardCheck extends BaseProcessor {
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return FRONT_SIDE_CARD_CHECK == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        MessageDto message = MessageDto.builder()
                .messageId(processableMessage.getMessageId())
                .messageText(processableMessage.getText())
                .build();
        userMessageHolder.setUserMessage(processableMessage.getUserId(), message);
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Передняя сторона: %s".formatted(processableMessage.getText()))
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+2705") + " Принять", CommandType.ACCEPT_FRONT_SIDE),
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " Изменить", CommandType.DECLINE_FRONT_SIDE),
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                        ))
                )
                .chatId(processableMessage.getChatId())
                .build();
    }
}
