package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.state.MessageDto;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.BaseProcessor;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.EmojiConverter;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.state.UserStateType.GIVE_NAME_TO_SET;

@Slf4j
@Component
@RequiredArgsConstructor
public class _2_GiveNameToSetCheck extends BaseProcessor {

    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return GIVE_NAME_TO_SET == userStateType;
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
                .chatId(processableMessage.getChatId())
                .text("Будет создан набор карточек: \"%s\"".formatted(processableMessage.getText()))
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+2705") + " Принять", CommandType.ACCEPT_SET_NAME),
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " Изменить", CommandType.DECLINE_SET_NAME),
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                        ))
                )
                .build();
        /*return BotReply.builder()
                .type(BotReplyType.DELETE_MESSAGE_TEXT)
                .messageId(processableMessage.getMessageId())
                .chatId(processableMessage.getChatId())
                .nextReply(BotReply.builder()
                        .type(BotReplyType.EDIT_MESSAGE_TEXT)
                        .messageId(processableMessage.getMessageId() - 1)
                        .text("Дано название: %s".formatted(processableMessage.getText()))
                        .replyMarkup(
                                MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                        Pair.of(EmojiConverter.getEmoji("U+2705") + " Принять", CommandType.ACCEPT_SET_NAME),
                                        Pair.of(EmojiConverter.getEmoji("U+274C") + " Отклонить", CommandType.DECLINE_SET_NAME)
                                ))
                        )
                        .chatId(processableMessage.getChatId())
                        .build())
                .build();*/
    }

}
