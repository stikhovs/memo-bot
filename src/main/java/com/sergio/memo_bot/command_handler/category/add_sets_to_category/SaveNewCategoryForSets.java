package com.sergio.memo_bot.command_handler.category.add_sets_to_category;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.category.add_sets_to_category.dto.AddSetToCategoryData;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.ApiCallService;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.NextReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveNewCategoryForSets implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SAVE_NEW_CATEGORY_FOR_SETS == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        AddSetToCategoryData addSetToCategoryData = chatTempDataService.mapDataToType(chatId, CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE, AddSetToCategoryData.class);

        apiCallService.updateCategoryBatch(addSetToCategoryData.getChosenCardSets().stream().map(CardSetDto::getId).toList(), addSetToCategoryData.getCategory().getId());


        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Успешно сохранено")
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.GET_CATEGORY_INFO_RESPONSE)
                        .build())
                .build();
    }
}