package com.sergio.memo_bot.command_handler.category;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
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

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseCardSetInCategory implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CHOOSE_CARD_SET_IN_CATEGORY == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        List<CardSetDto> cardSets = chatTempDataService.mapDataToList(chatId, CommandType.GET_CATEGORY_CARD_SET_INFO, CardSetDto.class);

        if (isEmpty(cardSets)) {
            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(YOU_DO_NOT_HAVE_CARD_SETS_IN_THIS_CATEGORY_YET)
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                            Pair.of(YES, CommandType.CREATE_SET_FOR_CHOSEN_CATEGORY),
                            Pair.of(NO, CommandType.GET_CATEGORY_INFO_RESPONSE)
                    )))
                    .build();
        }

        chatTempDataService.clearAndSave(chatId,
                ChatTempData.builder()
                        .chatId(chatId)
                        .data(new Gson().toJson(cardSets))
                        .command(CommandType.GET_ALL_SETS)
                        .build());

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOOSE_CARD_SET)
                .replyMarkup(getKeyboard(cardSets))
                .build();
    }

    private InlineKeyboardMarkup getKeyboard(List<CardSetDto> cardSets) {
        List<Pair<String, String>> buttonsPairs = cardSets.stream()
                .map(cardSet -> Pair.of(
                        cardSet.getTitle(),
                        CommandType.GET_CARD_SET_INFO.getCommandText().formatted(cardSet.getId())))
                .toList();

        List<InlineKeyboardRow> rows = MarkUpUtil.getKeyboardRows(buttonsPairs);

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text(BACK)
                        .callbackData(CommandType.GET_CATEGORY_INFO_RESPONSE.getCommandText())
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }


}
