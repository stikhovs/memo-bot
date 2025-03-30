package com.sergio.memo_bot.command_handler.create_set;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.MultipleBotReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class BackSideReceived implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.BACK_SIDE_RECEIVED == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {

        chatAwaitsInputService.clear(processableMessage.getChatId());
//        System.out.println("Before delete: " + chatAwaitsInputRepository.findAll());
//        chatAwaitsInputRepository.deleteByChatId(processableMessage.getChatId());
//        System.out.println("After delete: " + chatAwaitsInputRepository.findAll());

        ChatTempData chatTempData = chatTempDataService.get(processableMessage.getChatId());
        String updated = updateTempData(chatTempData, processableMessage.getText());
        ChatTempData updatedData = chatTempDataService.save(chatTempData.toBuilder().data(updated).build());

//        System.out.println(updatedData);
        CardSetDto cardSetDto = new Gson().fromJson(updatedData.getData(), CardSetDto.class);
        CardDto lastCard = cardSetDto.getCards().getLast();

        return MultipleBotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Добавлена карточка: %s - %s".formatted(lastCard.getFrontSide(), lastCard.getBackSide()))
                .messageId(processableMessage.getMessageId())
                .chatId(processableMessage.getChatId())
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.ADD_CARD_RESPONSE)
                .build();
    }

    @SneakyThrows
    private String updateTempData(ChatTempData tempData, String backSide) {
        Gson gson = new Gson();
//        System.out.println(tempData.getData());
        CardSetDto cardSetDto = gson.fromJson(tempData.getData(), CardSetDto.class);
        CardSetDto updated = cardSetDto.toBuilder()
                .cards(cardSetDto.getCards().stream().peek(cardDto -> {
                    if (StringUtils.isBlank(cardDto.getBackSide())) {
                        cardDto.setBackSide(backSide);
                    }
                }).toList())
                .build();
        return gson.toJson(updated);
    }
}
