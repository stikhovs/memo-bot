package com.sergio.memo_bot.path.create_set;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.UserInputState;
import com.sergio.memo_bot.state.UserStateType;
import com.sergio.memo_bot.update_handler.BaseProcessor;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.EmojiConverter;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.state.UserStateType.ADD_CARD_REQUEST;

@Slf4j
@Component
@RequiredArgsConstructor
public class _4_AddCard extends BaseProcessor {

    private final UserInputState userInputState;

    @Override
    public boolean canHandleByUserState(UserStateType userStateType) {
        return ADD_CARD_REQUEST == userStateType;
    }

    @Override
    public BotReply process(ProcessableMessage processableMessage) {
        userInputState.setUserInputState(processableMessage.getUserId(), true);
        return BotReply.builder()
                .type(BotReplyType.EDIT_MESSAGE_TEXT)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .text(EmojiConverter.getEmoji("U+2705") + " Добавляем карточку")
                /*.replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                Pair.of(EmojiConverter.getEmoji("U+274C") + " В начало", CommandType.MAIN_MENU)
                        ))
                )*/
                .nextReply(
                        BotReply.builder()
                                .type(BotReplyType.MESSAGE)
                                .chatId(processableMessage.getChatId())
                                .text("Передняя сторона карточки:")
                                .replyMarkup(MarkUpUtil.getForceReplyMarkup("Передняя сторона"))
                                .build()
                )
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
