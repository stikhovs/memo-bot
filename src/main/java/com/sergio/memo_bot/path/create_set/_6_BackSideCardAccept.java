package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.*;
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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class _6_BackSideCardAccept extends AbstractProcessable {

    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return BACK_SIDE_CARD_ACCEPT == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        userCardSetState.getUserCardSet(userId).flatMap(cardSetDto -> cardSetDto
                .getCards()
                .stream()
                .filter(cardDto -> isNotBlank(cardDto.getFrontSide()) && isBlank(cardDto.getBackSide()))
                .findFirst()).ifPresent(card -> card.setBackSide(userMessageHolder.getUserMessage(userId))
        );
        log.info("Добавлена задняя сторона: {}", userCardSetState.getUserCardSet(processableMessage.getUserId()));
        return BotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Отлично! Добавить еще карточку?")
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+2705") + " Да", CommandType.ADD_CARD),
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " Нет, сохранить набор", CommandType.SAVE_CARD_SET)
                        ))
                )
                .chatId(processableMessage.getChatId())
                .build();
    }

}
