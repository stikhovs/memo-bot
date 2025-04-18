package com.sergio.memo_bot.command_handler.exercise;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExercisesFromMainMenu implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXERCISES_FROM_MAIN_MENU == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        List<CardSetDto> cardSets = apiCallService.getCardSets(chatId);

        if (isEmpty(cardSets)) {
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
                        .command(CommandType.EXERCISES_FROM_MAIN_MENU_CHOOSE_SET)
                        .build());

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Выберите набор")
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
                        .text("Назад")
                        .callbackData(CommandType.MAIN_MENU.getCommandText())
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}