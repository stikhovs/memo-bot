package com.sergio.memo_bot.command_handler.card_set.create;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetCategoryRequest implements CommandHandler {

    private final CategoryHttpService categoryHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SET_CATEGORY_REQUEST == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        Optional<ChatTempData> optionalChosenCategory = chatTempDataService.find(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE);

        if (optionalChosenCategory.isEmpty()) {
            List<CategoryDto> categories = categoryHttpService.getByChatId(chatId);

            chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                    .chatId(chatId)
                    .command(CommandType.SET_CATEGORY_REQUEST)
                    .data(new Gson().toJson(categories))
                    .build());

            return BotMessageReply.builder()
                    .chatId(chatId)
                    .text(CHOOSE_CATEGORY_FOR_SET_CREATION)
                    .replyMarkup(getKeyboard(categories))
                    .parseMode(ParseMode.HTML)
                    .build();
        } else {
            return BotPartReply.builder()
                    .chatId(processableMessage.getChatId())
                    .previousProcessableMessage(processableMessage)
                    .nextCommand(CommandType.NAME_SET_REQUEST)
                    .build();
        }

    }

    private InlineKeyboardMarkup getKeyboard(List<CategoryDto> categories) {
        Map<String, String> buttonsMap = categories
                .stream()
                .filter(categoryDto -> !categoryDto.isDefault())
                .collect(Collectors.toMap(
                        CategoryDto::getTitle,
                        categoryDto -> CommandType.SET_CATEGORY_RESPONSE.getCommandText().formatted(categoryDto.getId())
                ));

        List<InlineKeyboardRow> rows = MarkUpUtil.getKeyboardRows(buttonsMap);

        categories
                .stream()
                .filter(CategoryDto::isDefault)
                .findFirst()
                .ifPresent(defaultCategory ->
                        rows.add(new InlineKeyboardRow(
                                InlineKeyboardButton.builder()
                                        .text(SKIP)
                                        .callbackData(CommandType.SET_CATEGORY_RESPONSE.getCommandText().formatted(defaultCategory.getId()))
                                        .build()
                        ))
                );

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text(CREATE_CATEGORY)
                        .callbackData(CommandType.CREATE_CATEGORY_REQUEST.getCommandText())
                        .build()
        ));

        rows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text(BACK)
                        .callbackData(CommandType.CARD_SET_MENU.getCommandText())
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}