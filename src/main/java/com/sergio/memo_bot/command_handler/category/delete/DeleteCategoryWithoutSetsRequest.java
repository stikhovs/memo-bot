package com.sergio.memo_bot.command_handler.category.delete;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteCategoryWithoutSetsRequest implements CommandHandler {

    private final CategoryHttpService categoryHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.DELETE_CATEGORY_WITHOUT_SETS_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        CategoryDto categoryDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CATEGORY_INFO_RESPONSE, CategoryDto.class);

        categoryHttpService.delete(categoryDto.getId(), true);

        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.DELETE_CATEGORY_RESPONSE)
                .build();
    }
}