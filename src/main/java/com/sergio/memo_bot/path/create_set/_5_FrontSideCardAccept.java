package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.*;
import com.sergio.memo_bot.update_handler.AbstractProcessable;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.state.UserStateType.FRONT_SIDE_CARD_ACCEPT;

@Slf4j
@Component
@RequiredArgsConstructor
public class _5_FrontSideCardAccept extends AbstractProcessable {
    private final UserCardSetState userCardSetState;
    private final UserMessageHolder userMessageHolder;
    private final UserInputState userInputState;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return FRONT_SIDE_CARD_ACCEPT == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        Long userId = processableMessage.getUserId();
        userInputState.setUserInputState(userId, true);
        String userMessage = userMessageHolder.getUserMessage(userId);
        if (processableMessage.isProcessable()) {
            userCardSetState.getUserCardSet(userId)
                    .ifPresent(cardSetDto -> cardSetDto
                            .getCards().add(
                                    CardDto.builder()
                                            .frontSide(userMessage)
                                            .build()
                            ));
            log.info("Добавлена передняя сторона: {}", userCardSetState.getUserCardSet(processableMessage.getUserId()));

            return BotReply.builder()
                    .type(BotReplyType.EDIT_MESSAGE_TEXT)
                    .messageId(processableMessage.getMessageId())
                    .text(EmojiConverter.getEmoji("U+2705") + " Передняя сторона: %s".formatted(userMessage))
                    .replyMarkup(
                            MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                    Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                            ))
                    )
                    .nextReply(BotReply.builder()
                            .type(BotReplyType.FORCE_REPLY)
                            .text("Обратная сторона карточки:")
                            .replyMarkup(MarkUpUtil.getForceReplyMarkup("Обратная сторона"))
                            .chatId(processableMessage.getChatId())
                            .build())
                    .chatId(processableMessage.getChatId())
                    .build();
        } else {
            log.info("Передняя сторона не добавлена");
            return BotReply.builder()
                    .type(BotReplyType.FORCE_REPLY)
                    .text("Обратная сторона карточки:")
                    .replyMarkup(MarkUpUtil.getForceReplyMarkup("Обратная сторона"))
                    .chatId(processableMessage.getChatId())
                    .build();
        }
    }

    /*@Override
    public boolean canProcess(CallbackQuery callbackQuery) {
        return userPathState.getUserState(callbackQuery.getFrom().getId()) == PathState.CARD_CREATION_FRONT_SIDE
                && callbackQuery.getData().equals("Yes");
    }

    @Override
    public BotApiMethodMessage process(CallbackQuery callbackQuery, Long chatId) {
        Long userId = callbackQuery.getFrom().getId();
        userPathState.setUserState(userId, PathState.CARD_CREATION_BACK_SIDE);
        userCardSetState.getUserCardSet(userId)
                .ifPresent(cardSetDto -> cardSetDto
                        .getCards().add(
                                CardDto.builder()
                                        .frontSide(userMessageHolder.getUserMessage(userId))
                                        .build()
                        ));
        return SendMessage.builder()
                .text("Обратная сторона карточки:")
                .chatId(chatId)
                .build();
    }*/
}
