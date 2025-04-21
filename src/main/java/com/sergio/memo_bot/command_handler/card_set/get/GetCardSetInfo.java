package com.sergio.memo_bot.command_handler.card_set.get;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.external.ApiCallService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.size;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCardSetInfo implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ApiCallService apiCallService;

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
            chosenCardSet = apiCallService.getCardSet(chosenCardSetId).orElseThrow();
        }

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                .chatId(chatId)
                .data(new Gson().toJson(chosenCardSet))
                .command(CommandType.GET_CARD_SET_INFO)
                .build());

        CategoryDto category = apiCallService.getCategoryById(chosenCardSet.getCategoryId());



        /*cardSet.ifPresent(cardSetDto ->
            chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                    .chatId(chatId)
                    .data(new Gson().toJson(cardSetDto))
                    .command(CommandType.GET_CARD_SET_INFO)
                    .build())
        );*/

        return BotMessageReply.builder()
                .chatId(chatId)
                .text("Набор  \"%s\". ".formatted(chosenCardSet.getTitle()) +
                        "\n\nКатегория: %s ".formatted(category.getTitle()) +
                        "\n\nКоличество карточек: %s ".formatted(size(chosenCardSet.getCards())))
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of("Посмотреть карточки", CommandType.GET_CARDS),
                        Pair.of("Редактировать набор", CommandType.EDIT_SET),
                        CollectionUtils.isNotEmpty(chosenCardSet.getCards())
                                ? Pair.of("Упражнения", CommandType.EXERCISES_FROM_CARD_SET)
                                : Pair.of(null, null),
                        Pair.of("Назад", CommandType.GET_ALL_SETS)
                )))
                .build();

    }

}
