package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.state.UserCardSetState;
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

import java.util.ArrayList;
import java.util.List;

import static com.sergio.memo_bot.state.UserStateType.SET_NAME_APPROVAL;

@Slf4j
@Component
@RequiredArgsConstructor
public class _3_GiveNameToSetAccept extends BaseProcessor {

    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return SET_NAME_APPROVAL == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        String userMessage = userMessageHolder.getUserMessage(processableMessage.getUserId()).getMessageText();
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
                /*.replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                        ))
                )*/
                .nextReply(BotReply.builder()
                        .type(BotReplyType.MESSAGE)
                        .chatId(processableMessage.getChatId())
                        .text("Теперь давайте добавим в него карточки")
                        .replyMarkup(
                                MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                        Pair.of(EmojiConverter.getEmoji("U+2705") + " Добавить карточку", CommandType.ADD_CARD_REQUEST),
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
