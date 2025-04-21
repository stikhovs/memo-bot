package com.sergio.memo_bot.command_handler.card_set.create;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.CategoryDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.external.ApiCallService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveSetRequest implements CommandHandler {

    private final ApiCallService apiCallService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.SAVE_CARD_SET_REQUEST == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(chatId, CommandType.ADD_CARD_RESPONSE, CardSetDto.class);

        Long categoryId = chatTempDataService.find(chatId, CommandType.GET_CATEGORY_INFO_RESPONSE)
                .map(chatTempData -> new Gson().fromJson(chatTempData.getData(), CategoryDto.class))
                .map(CategoryDto::getId)
                .orElse(null);


        Runnable action = getAction(chatId, cardSetDto.toBuilder().categoryId(categoryId).build());

        if (callSafely(action)) {
            chatTempDataService.clear(chatId);

            return BotPartReply.builder()
                    .nextCommand(CommandType.SAVE_CARD_SET_RESPONSE)
                    .previousProcessableMessage(processableMessage)
//                    .messageId(processableMessage.getMessageId())
                    .chatId(chatId)
                    .build();
        } else {
            log.warn("Что-то пошло не так");
            return BotMessageReply.builder()
//                    .type(BotReplyType.MESSAGE)
                    .text("Что-то пошло не так")
                    .chatId(chatId)
                    .build();
        }
    }

    private Runnable getAction(Long chatId, CardSetDto cardSetDto) {
        Runnable action;
        if (chatTempDataService.find(chatId, CommandType.GET_CARD_SET_INFO).isPresent()) {
            action = () -> apiCallService.addCard(cardSetDto.getId(), cardSetDto.getCards().getLast());
        } else {
            action = () -> apiCallService.saveCardSet(cardSetDto);
        }
        return action;
    }

    private boolean callSafely(Runnable action) {
        try {
            action.run();
            return true;
        } catch (RuntimeException ex) {
            log.error("Something went wrong when updating cardSet", ex);
            return false;
        }
    }

}
