package com.sergio.memo_bot.command_handler.exercise;

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
public class ExercisesFromMainMenu implements CommandHandler {

    private final CardSetHttpService cardSetHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXERCISES_FROM_MAIN_MENU == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        List<CardSetDto> cardSets = cardSetHttpService.getCardSets(chatId);

        if (isEmpty(cardSets)) {
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
                        .command(CommandType.EXERCISES_FROM_MAIN_MENU_CHOOSE_SET)
                        .build());

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(CHOOSE_CARD_SET)
                .replyMarkup(getKeyboard(cardSets))
                .build();
    }

    private InlineKeyboardMarkup getKeyboard(List<CardSetDto> cardSets) {
        Map<String, String> buttonsMap = cardSets
                .stream()
                .collect(Collectors.toMap(
                        CardSetDto::getTitle,
                        cardSet -> CommandType.EXERCISES_FROM_MAIN_MENU_CHOOSE_SET.getCommandText().formatted(cardSet.getId())
                ));

        List<InlineKeyboardRow> rows = MarkUpUtil.getKeyboardRows(buttonsMap);

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text(BACK)
                        .callbackData(CommandType.MAIN_MENU.getCommandText())
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}