package com.sergio.memo_bot.command_handler.card.create;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.NextReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        return BotMessageReply.builder()
//                .type(BotReplyType.MESSAGE)
                .chatId(chatId)
                .text("Добавлена карточка: %s - %s".formatted(frontSide.getData(), backSide.getData()))
//                .messageId(processableMessage.getMessageId())
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.ADD_CARD_RESPONSE)
                        .previousProcessableMessage(processableMessage)
                        .build())
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
