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
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);
//        System.out.println("Before delete: " + chatAwaitsInputRepository.findAll());
//        chatAwaitsInputRepository.deleteByChatId(processableMessage.getChatId());
//        System.out.println("After delete: " + chatAwaitsInputRepository.findAll());

//        ChatTempData chatTempData = chatTempDataService.get(processableMessage.getChatId(), CommandType.BACK_SIDE_RECEIVED);
//        String updated = updateTempData(chatTempData, processableMessage.getText());
        ChatTempData backSide = chatTempDataService.clearAndSave(chatId,
                ChatTempData.builder()
                        .chatId(chatId)
                        .command(CommandType.BACK_SIDE_RECEIVED)
                        .data(processableMessage.getText())
                        .build()
        );
        ChatTempData frontSide = chatTempDataService.get(chatId, CommandType.FRONT_SIDE_RECEIVED);

        return MultipleBotReply.builder()
                .type(BotReplyType.MESSAGE)
                .text("Добавлена карточка: %s - %s".formatted(frontSide.getData(), backSide.getData()))
                .messageId(processableMessage.getMessageId())
                .chatId(chatId)
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.ADD_CARD_RESPONSE)
                .build();
    }

    /*@SneakyThrows
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
    }*/
}
