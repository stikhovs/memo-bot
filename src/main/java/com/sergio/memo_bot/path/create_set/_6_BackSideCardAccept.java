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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class _6_BackSideCardAccept extends BaseProcessor {

    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return BACK_SIDE_CARD_ACCEPT == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        String userMessage = userMessageHolder.getUserMessage(userId).getMessageText();
        userCardSetState.getUserCardSet(userId).flatMap(cardSetDto -> cardSetDto
                .getCards()
                .stream()
                .filter(cardDto -> isNotBlank(cardDto.getFrontSide()) && isBlank(cardDto.getBackSide()))
                .findFirst()).ifPresent(card -> card.setBackSide(userMessage)
        );
        log.info("Добавлена задняя сторона: {}", userCardSetState.getUserCardSet(processableMessage.getUserId()));
        return BotReply.builder()
                .type(BotReplyType.EDIT_MESSAGE_TEXT)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .text(EmojiConverter.getEmoji("U+2705") + " Задняя сторона: %s".formatted(userMessage))
                /*.replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                        ))
                )*/
                .nextReply(BotReply.builder()
                        .type(BotReplyType.MESSAGE)
                        .text("Отлично! Карточка будет добавлена в набор")
                        .replyMarkup(
                                MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                        Pair.of(EmojiConverter.getEmoji("U+2705") + " Добавить еще одну", CommandType.ADD_CARD),
                                        Pair.of(EmojiConverter.getEmoji("U+274C") + " Сохранить набор", CommandType.SAVE_CARD_SET),
                                        Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                                ))
                        )
                        .chatId(processableMessage.getChatId())
                        .build())
                .build();
    }

}
