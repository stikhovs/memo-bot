package com.sergio.memo_bot.command_handler.card_set.get;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.card_set.CardSetHttpService;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;
import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCardSetInfo implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final CardSetHttpService cardSetHttpService;
    private final CategoryHttpService categoryHttpService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_CARD_SET_INFO == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardSetDto chosenCardSet;
        String[] commandAndCardSetId = processableMessage.getText().split("__");

        if (commandAndCardSetId[1].equals("%s")) {
            chosenCardSet = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);
        } else {
            Long chosenCardSetId = Long.valueOf(commandAndCardSetId[1]);
            chosenCardSet = cardSetHttpService.getCardSet(chosenCardSetId);
        }

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .data(new Gson().toJson(chosenCardSet))
                .command(CommandType.GET_CARD_SET_INFO)
                .build());

        CategoryDto category = categoryHttpService.getById(chosenCardSet.getCategoryId());

        String text = category.isDefault()
                ? CARD_SET_INFO_NO_CATEGORY.formatted(chosenCardSet.getTitle(), size(chosenCardSet.getCards()))
                : CARD_SET_INFO.formatted(
                chosenCardSet.getTitle(),
                category.getTitle(),
                size(chosenCardSet.getCards()));

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(SEE_CARDS, CommandType.GET_CARDS),
                        Pair.of(ADD_CARD, CommandType.ADD_CARD_REQUEST),
                        Pair.of(EDIT_CARD_SET, CommandType.EDIT_SET),
                        CollectionUtils.isNotEmpty(chosenCardSet.getCards())
                                ? Pair.of(EXERCISES, CommandType.EXERCISES_FROM_CARD_SET)
                                : Pair.of(null, null),
                        Pair.of(BACK, CommandType.GET_ALL_SETS)
                )))
                .parseMode(ParseMode.HTML)
                .build();

    }

}
