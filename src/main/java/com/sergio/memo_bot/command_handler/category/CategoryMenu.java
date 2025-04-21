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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryMenu implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CATEGORY_MENU == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        List<CategoryDto> categories = chatTempDataService.mapDataToList(chatId, CommandType.CATEGORY_MENU_DATA, CategoryDto.class);

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Категории")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        isNotEmpty(categories) ? Pair.of("Выбрать категорию", CommandType.CHOOSE_CATEGORY_REQUEST) : Pair.of(null, null),
                        Pair.of("Создать категорию", CommandType.CREATE_CATEGORY_REQUEST),
                        Pair.of("Назад", CommandType.MAIN_MENU)
                )))
                .build();
    }
}