package com.sergio.memo_bot.command_handler.category.add_sets_to_category;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.category.add_sets_to_category.dto.AddSetToCategoryData;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.card_set.CardSetHttpService;
import com.sergio.memo_bot.external.http.category.CategoryHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.SUCCESSFULLY_SAVED;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveNewCategoryForSets implements CommandHandler {

    private final CardSetHttpService cardSetHttpService;
    private final CategoryHttpService categoryHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SAVE_NEW_CATEGORY_FOR_SETS == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        NextReply nextReply;

        Optional<ChatTempData> addSetToCategoryDataOptional = chatTempDataService.find(chatId, CommandType.CHOOSE_SETS_FOR_CATEGORY_PREPARE);
        if (addSetToCategoryDataOptional.isPresent()) {
            AddSetToCategoryData addSetToCategoryData = addSetToCategoryDataOptional.map(it -> new Gson().fromJson(it.getData(), AddSetToCategoryData.class)).get();
            cardSetHttpService.updateCategoryBatch(addSetToCategoryData.getCategory().getId(), addSetToCategoryData.getChosenCardSets().stream().map(CardSetDto::getId).toList());

            CategoryDto updatedCategory = categoryHttpService.getById(addSetToCategoryData.getCategory().getId());
            List<CardSetDto> movedCardSets = cardSetHttpService.getCardSetsByCategory(addSetToCategoryData.getCategory().getId());

            chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                    .chatId(chatId)
                    .command(CommandType.GET_CATEGORY_INFO_RESPONSE)
                    .data(new Gson().toJson(updatedCategory))
                    .build());

            chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                    .chatId(chatId)
                    .command(CommandType.GET_CATEGORY_CARD_SET_INFO)
                    .data(new Gson().toJson(movedCardSets))
                    .build());

            nextReply = NextReply.builder()
                    .previousProcessableMessage(processableMessage)
                    .nextCommand(CommandType.GET_CATEGORY_INFO_RESPONSE)
                    .build();
        } else {
            CategoryDto categoryDto = chatTempDataService.mapDataToType(chatId, CommandType.CHOOSE_CATEGORY_FOR_SET_MOVING, CategoryDto.class);
            CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);

            cardSetHttpService.updateCategoryBatch(categoryDto.getId(), List.of(cardSetDto.getId()));
            CardSetDto updatedCardSet = cardSetHttpService.getCardSet(cardSetDto.getId());

            chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                            .chatId(chatId)
                            .command(CommandType.GET_CARD_SET_INFO)
                            .data(new Gson().toJson(updatedCardSet))
                    .build());

            nextReply = NextReply.builder()
                    .previousProcessableMessage(processableMessage.toBuilder().text(CommandType.GET_CARD_SET_INFO.getCommandText()).build())
                    .nextCommand(CommandType.GET_CARD_SET_INFO)
                    .build();
        }

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(SUCCESSFULLY_SAVED)
                .nextReply(nextReply)
                .build();
    }
}