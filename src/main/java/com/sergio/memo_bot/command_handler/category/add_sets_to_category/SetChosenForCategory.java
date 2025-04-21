package com.sergio.memo_bot.command_handler.category.add_sets_to_category;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.category.add_sets_to_category.dto.AddSetToCategoryData;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetChosenForCategory implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SET_CHOSEN_FOR_CATEGORY == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        String[] commandAndCardSetId = processableMessage.getText().split("__");
        Long cardSetId = Long.valueOf(commandAndCardSetId[1]);

        AddSetToCategoryData addSetToCategoryData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE, AddSetToCategoryData.class);

        CardSetDto chosenCardSet = addSetToCategoryData.getAllCardSets().stream().filter(cardSetDto -> cardSetDto.getId().equals(cardSetId)).findFirst().get();

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE)
                .data(new Gson().toJson(
                        addSetToCategoryData.toBuilder()
                                .allCardSets(remove(addSetToCategoryData.getAllCardSets(), chosenCardSet))
                                .chosenCardSets(add(addSetToCategoryData.getChosenCardSets(), chosenCardSet))
                                .build()))
                .build());

        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.CHOOSE_SETS_FOR_CATEGORY_REQUEST)
                .build();
    }

    private List<CardSetDto> remove(List<CardSetDto> list, CardSetDto chosenCardSet) {
        return list.stream().filter(cardSetDto -> !cardSetDto.getId().equals(chosenCardSet.getId()))
                .toList();
    }

    private List<CardSetDto> add(List<CardSetDto> list, CardSetDto chosenCardSet) {
        list.add(chosenCardSet);
        return list;
    }
}