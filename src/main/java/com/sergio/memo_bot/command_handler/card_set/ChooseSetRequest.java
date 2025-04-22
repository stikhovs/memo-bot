package com.sergio.memo_bot.command_handler.card_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseSetRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CHOOSE_SET_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        List<CardSetDto> cardSets = chatTempDataService.mapDataToList(chatId, CommandType.CARD_SET_MENU_DATA, CardSetDto.class);

        if (isEmpty(cardSets)) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(YOU_DO_NOT_HAVE_CARD_SETS_YET)
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                            Pair.of(YES, CommandType.SET_CREATION_START),
                            Pair.of(NO, CommandType.CARD_SET_MENU)
                    )))
                    .build();
        }

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOOSE_CARD_SET)
                .replyMarkup(getKeyboard(cardSets))
                .build();
    }

    private InlineKeyboardMarkup getKeyboard(List<CardSetDto> cardSets) {
        Map<String, String> buttonsMap = cardSets
                .stream()
                .collect(Collectors.toMap(
                        CardSetDto::getTitle,
                        cardSet -> CommandType.GET_CARD_SET_INFO.getCommandText().formatted(cardSet.getId())
                ));

        List<InlineKeyboardRow> rows = MarkUpUtil.getKeyboardRows(buttonsMap);

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text(BACK)
                        .callbackData(CommandType.CARD_SET_MENU.getCommandText())
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}