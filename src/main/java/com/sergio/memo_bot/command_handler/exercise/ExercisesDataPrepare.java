package com.sergio.memo_bot.command_handler.exercise;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.card_set.CardSetHttpService;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExercisesDataPrepare implements CommandHandler {

    private final CardSetHttpService cardSetHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.EXERCISES_DATA_PREPARE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CommandType sourceCommand = defineSource(chatId);

        List<CardDto> cards = defineCards(chatId, sourceCommand);
        Collections.shuffle(cards);

        ExerciseData exerciseData = ExerciseData.builder()
                .cards(cards)
                .build();

        chatTempDataService.clearAndSave(chatId, ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.EXERCISES_RESPONSE)
                        .data(new Gson().toJson(exerciseData))
                .build());

        return BotPartReply.builder()
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.EXERCISES_DATA_SIZE_CHECK)
                .build();
    }

    private List<CardDto> defineCards(Long chatId, CommandType sourceCommand) {
        return switch (sourceCommand) {
            case EXERCISES_FROM_CARD_SET ->
                    chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class).getCards();
            case EXERCISES_FROM_CATEGORY ->
                    chatTempDataService.mapDataToList(chatId, CommandType.GET_CATEGORY_CARD_SET_INFO, CardSetDto.class).stream()
                            .map(cardSetDto -> cardSetHttpService.getCardSet(cardSetDto.getId()))
                            .flatMap(cardSetDto -> cardSetDto.getCards().stream())
                            .collect(Collectors.toList());
            default ->
                    chatTempDataService.mapDataToType(chatId, CommandType.EXERCISES_FROM_MAIN_MENU_CHOOSE_SET, CardSetDto.class).getCards();
        };
    }

    private CommandType defineSource(Long chatId) {
        return chatTempDataService.find(chatId, CommandType.EXERCISES_DATA_PREPARE)
                .map(chatTempData -> CommandType.getByCommandText(chatTempData.getData()))
                .orElse(CommandType.EXERCISES_FROM_MAIN_MENU);
    }
}
