package com.sergio.memo_bot.command_handler.card_set.get;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.card_set.CardSetHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAllSetsRequest implements CommandHandler {

    private final CardSetHttpService cardSetHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_ALL_SETS == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        List<CardSetDto> cardSets = cardSetHttpService.getCardSets(processableMessage.getChatId());

        if (CollectionUtils.isEmpty(cardSets)) {
            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text(YOU_DO_NOT_HAVE_CARD_SETS_YET)
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboardRow(new InlineKeyboardRow(
                                    InlineKeyboardButton.builder().text(YES).callbackData(CommandType.NAME_SET_REQUEST.getCommandText()).build(),
                                    InlineKeyboardButton.builder().text(NO).callbackData(CommandType.MAIN_MENU.getCommandText()).build()
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
                .text(CHOOSE_CARD_SET)
                .replyMarkup(MarkUpUtil.getInlineCardSetButtons(cardSets))
                .build();
    }

}
