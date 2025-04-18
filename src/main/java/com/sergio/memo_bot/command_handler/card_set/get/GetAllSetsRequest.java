package com.sergio.memo_bot.command_handler.card_set.get;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.ApiCallService;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllSetsRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ApiCallService apiCallService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_ALL_SETS == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        List<CardSetDto> cardSets = apiCallService.getCardSets(processableMessage.getChatId());

        if (CollectionUtils.isEmpty(cardSets)) {
            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text("Вы пока не создали ни одного набора. Хотите создать сейчас?")
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboardRow(new InlineKeyboardRow(
                                    InlineKeyboardButton.builder().text("Да").callbackData(CommandType.NAME_SET_REQUEST.getCommandText()).build(),
                                    InlineKeyboardButton.builder().text("Нет").callbackData(CommandType.MAIN_MENU.getCommandText()).build()
                            ))
                            .build())
                    .build();
        }

        chatTempDataService.clearAndSave(processableMessage.getChatId(),
                ChatTempData.builder()
                        .chatId(processableMessage.getChatId())
                        .data(new Gson().toJson(cardSets))
                        .command(CommandType.GET_ALL_SETS)
                        .build());

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Выберите набор")
                .replyMarkup(MarkUpUtil.getInlineCardSetButtons(cardSets))
                .build();
    }

}
