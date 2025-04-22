package com.sergio.memo_bot.command_handler.category;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.reply.Reply;
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
public class ChooseCategoryRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CHOOSE_CATEGORY_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        List<CategoryDto> categories = chatTempDataService.mapDataToList(chatId, CommandType.CATEGORY_MENU_DATA, CategoryDto.class);

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
                        CategoryDto::getTitle,
                        categoryDto -> CommandType.GET_CATEGORY_INFO_REQUEST.getCommandText().formatted(categoryDto.getId())
                ));

        List<InlineKeyboardRow> rows = MarkUpUtil.getKeyboardRows(buttonsMap);

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text(BACK)
                        .callbackData(CommandType.CATEGORY_MENU.getCommandText())
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

}