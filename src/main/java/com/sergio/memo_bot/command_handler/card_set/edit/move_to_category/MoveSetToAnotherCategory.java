package com.sergio.memo_bot.command_handler.card_set.edit.move_to_category;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
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
import java.util.Map;
import java.util.stream.Collectors;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoveSetToAnotherCategory implements CommandHandler {

    private final CategoryHttpService categoryHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.MOVE_SET_TO_ANOTHER_CATEGORY == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class);
        List<CategoryDto> categories = categoryHttpService.getByChatId(chatId)
                .stream()
                .filter(categoryDto -> !categoryDto.getId().equals(cardSetDto.getCategoryId()))
                .toList();

        if (isEmpty(categories)) {
            return BotMessageReply.builder()
                    .chatId(processableMessage.getChatId())
                    .text(YOU_DO_NOT_HAVE_CATEGORIES_YET)
                    .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                            Pair.of(YES, CommandType.CREATE_CATEGORY_REQUEST),
                            Pair.of(NO, CommandType.EDIT_SET)
                    )))
                    .build();
        }

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.MOVE_SET_TO_ANOTHER_CATEGORY)
                        .data(new Gson().toJson(categories))
                .build());

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CHOOSE_CATEGORY)
                .replyMarkup(getKeyboard(categories))
                .build();
    }

    private InlineKeyboardMarkup getKeyboard(List<CategoryDto> categories) {
        Map<String, String> buttonsMap = categories
                .stream()
                .collect(Collectors.toMap(
                        categoryDto -> categoryDto.isDefault() ? "Убрать из категории" : categoryDto.getTitle(),
                        categoryDto -> CommandType.CHOOSE_CATEGORY_FOR_SET_MOVING.getCommandText().formatted(categoryDto.getId())
                ));

        List<InlineKeyboardRow> rows = MarkUpUtil.getKeyboardRows(buttonsMap);

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text(BACK)
                        .callbackData(CommandType.EDIT_SET.getCommandText())
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}