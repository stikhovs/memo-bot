package com.sergio.memo_bot.command_handler.create_set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.AwaitsUserInput;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.repository.ChatAwaitsInputRepository;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class FrontSideReceived implements CommandHandler {

    private final ChatAwaitsInputRepository chatAwaitsInputRepository;
    private final ChatTempDataRepository chatTempDataRepository;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.FRONT_SIDE_RECEIVED == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {

        AwaitsUserInput awaitsUserInput = chatAwaitsInputRepository.findOneByChatId(processableMessage.getChatId())
                .map(aui -> aui.toBuilder()
                        .inputType("TEXT")
                        .nextCommand(CommandType.INSERT_BACK_SIDE.getCommandText())
                        .chatId(processableMessage.getChatId())
                        .build())
                .orElseThrow(() -> new RuntimeException("Must find the record by chatId"));

        chatAwaitsInputRepository.save(awaitsUserInput);

        List<ChatTempData> tempDataList = chatTempDataRepository.findByChatId(processableMessage.getChatId());
        if (CollectionUtils.isEmpty(tempDataList)) {
            throw new RuntimeException("Temp data should not be empty on this step");
        }

        ChatTempData tempData = tempDataList.getFirst();
        String updated = updateTempData(tempData, processableMessage.getText());
        chatTempDataRepository.save(tempData.toBuilder().data(updated).build());

        System.out.println(chatTempDataRepository.findByChatId(processableMessage.getChatId()));

        return BotPartReply.builder()
                .type(BotReplyType.MESSAGE)
                .nextCommand(CommandType.INSERT_BACK_SIDE)
                .messageId(processableMessage.getMessageId())
                .previousProcessableMessage(processableMessage)
                .chatId(processableMessage.getChatId())
                .build();
    }

    @SneakyThrows
    private String updateTempData(ChatTempData tempData, String frontSide) {
        Gson gson = new Gson();
        System.out.println(tempData.getData());
        CardSetDto cardSetDto = gson.fromJson(tempData.getData(), CardSetDto.class);
        CardSetDto updated;
        if (CollectionUtils.isEmpty(cardSetDto.getCards())) {
            updated = cardSetDto.toBuilder()
                    .cards(List.of(CardDto.builder().frontSide(frontSide).build()))
                    .build();
        } else {
            List<CardDto> cards = cardSetDto.getCards();
            cards.add(CardDto.builder().frontSide(frontSide).build());
            updated = cardSetDto.toBuilder()
                    .cards(cards)
                    .build();
        }
        return gson.toJson(updated);
    }
}
