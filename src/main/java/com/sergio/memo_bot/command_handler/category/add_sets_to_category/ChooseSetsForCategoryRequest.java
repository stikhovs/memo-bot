package com.sergio.memo_bot.command_handler.category.add_sets_to_category;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.category.add_sets_to_category.dto.AddSetToCategoryData;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
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
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseSetsForCategoryRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CHOOSE_SETS_FOR_CATEGORY_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        AddSetToCategoryData addSetToCategoryData = chatTempDataService.mapDataToType(chatId, CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE, AddSetToCategoryData.class);

        String text;

        if (CollectionUtils.isEmpty(addSetToCategoryData.getChosenCardSets())) {
            text = "Выберите наборы, которые будут перенесены в категорию \"%s\"".formatted(addSetToCategoryData.getCategory().getTitle());
        } else {
            text = ("Выберите наборы, которые будут перенесены в категорию \"%s\" \n\n".formatted(addSetToCategoryData.getCategory().getTitle()) +
                    "Выбранные наборы: \n\n %s".formatted(addSetToCategoryData.getChosenCardSets().stream().map(CardSetDto::getTitle).collect(Collectors.joining(";\n"))));
        }

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(getKeyboard(addSetToCategoryData))
                .build();
    }

    private static InlineKeyboardMarkup getKeyboard(AddSetToCategoryData addSetToCategoryData) {
        List<InlineKeyboardRow> rows = MarkUpUtil.getKeyboardRows(addSetToCategoryData.getAllCardSets().stream()
                .collect(Collectors.toMap(
                        CardSetDto::getTitle,
                        cardSetDto -> CommandType.SET_CHOSEN_FOR_CATEGORY.getCommandText().formatted(cardSetDto.getId())
                )));

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder().text("Сохранить").callbackData(CommandType.SAVE_NEW_CATEGORY_FOR_SETS.getCommandText()).build(),
                InlineKeyboardButton.builder().text("Назад").callbackData(CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE.getCommandText()).build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}