package com.sergio.memo_bot.command_handler.category.get;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
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
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.List;
import java.util.stream.Collectors;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCategoryInfoResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_CATEGORY_INFO_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CategoryDto categoryDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE, CategoryDto.class);
        List<CardSetDto> cardSets = chatTempDataService.mapDataToList(chatId, CommandType.GET_CATEGORY_CARD_SET_INFO, CardSetDto.class);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(isNotEmpty(cardSets)
                        ? CATEGORY_INFO_WITH_CARD_SETS.formatted(
                        categoryDto.getTitle(),
                        size(cardSets),
                        cardSets.stream().map(CardSetDto::getTitle).collect(Collectors.joining(";\n")))
                        : CATEGORY_INFO_WITHOUT_CARD_SETS.formatted(
                        categoryDto.getTitle(),
                        size(cardSets))
                )
                .parseMode(ParseMode.HTML)
                .replyMarkup(
                        MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                                isNotEmpty(cardSets) ? Pair.of(CHOOSE_CARD_SETS, CommandType.CHOOSE_CARD_SET_IN_CATEGORY) : Pair.of(null, null),
                                Pair.of(CREATE_CARD_SET_IN_THIS_CATEGORY, CommandType.CREATE_SET_FOR_CHOSEN_CATEGORY),
                                Pair.of(MOVE_CARD_SETS_TO_THIS_CATEGORY, CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE),
                                categoryDto.isDefault() ? Pair.of(null, null) : Pair.of(EDIT_THIS_CATEGORY, CommandType.EDIT_CATEGORY_REQUEST),
                                isNotEmpty(cardSets) ? Pair.of(EXERCISES, CommandType.EXERCISES_FROM_CATEGORY) : Pair.of(null, null),
                                Pair.of(BACK, CommandType.CHOOSE_CATEGORY_REQUEST)
                        )))
                .build();
    }
}