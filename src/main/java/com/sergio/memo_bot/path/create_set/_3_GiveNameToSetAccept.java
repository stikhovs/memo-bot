package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.state.UserCardSetState;
import com.sergio.memo_bot.state.UserMessageHolder;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.EmojiConverter;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sergio.memo_bot.state.UserStateType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class _3_GiveNameToSetAccept extends AbstractProcessable {

    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return SET_NAME_APPROVAL == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        String userMessage = userMessageHolder.getUserMessage(processableMessage.getUserId());
        userCardSetState.setUserCardSet(processableMessage.getUserId(), CardSetDto.builder()
                .telegramChatId(processableMessage.getChatId())
                .title(userMessage)
                .cards(new ArrayList<>())
                .build());
        log.info("Создан предварительный набор: {}", userCardSetState.getUserCardSet(processableMessage.getUserId()));
        return BotReply.builder()
                .type(BotReplyType.EDIT_MESSAGE_TEXT)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .text(EmojiConverter.getEmoji("U+2705") + " Будет создан набор: \"%s\"".formatted(userMessage))
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                        ))
                )
                .nextReply(BotReply.builder()
                        .type(BotReplyType.MESSAGE)
                        .chatId(processableMessage.getChatId())
                        .text("Теперь давайте добавим в него карточки")
                        .replyMarkup(
                                MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                        Pair.of(EmojiConverter.getEmoji("U+2705") + " Добавить карточку", CommandType.ADD_CARD),
                                        Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                                ))
                        )
                        .build())
                /*.nextReply(BotReply.builder()
                        .type(BotReplyType.EDIT_MESSAGE_TEXT)
                        .messageId(processableMessage.getMessageId())
                        .text("Теперь давайте добавим в набор карточки")
                        .chatId(processableMessage.getChatId())
                        .build())*/
                .build();
    }
}
