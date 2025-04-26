package com.sergio.memo_bot.command_handler.card_set.import_card_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
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

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.BACK;
import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CHOOSE_CATEGORY;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChooseCategoryForImportRequest implements CommandHandler {

    private final CategoryHttpService categoryHttpService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CHOOSE_CATEGORY_FOR_IMPORT_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        List<CategoryDto> categories = categoryHttpService.getByChatId(chatId);

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
                        categoryDto -> categoryDto.isDefault() ? "Пропустить" : categoryDto.getTitle(),
                        categoryDto -> CommandType.CHOOSE_CATEGORY_FOR_IMPORT_RESPONSE.getCommandText().formatted(categoryDto.getId())
                ));
        List<InlineKeyboardRow> rows = MarkUpUtil.getKeyboardRows(buttonsMap);

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder().text(BACK).callbackData(CommandType.IMPORT_CARD_SET_MENU.getCommandText()).build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}