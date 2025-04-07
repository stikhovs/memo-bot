package com.sergio.memo_bot.command_handler.create_set;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.ApiCallService;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
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

        Runnable action = getAction(chatId, cardSetDto);

        if (callSafely(action)) {
            chatTempDataService.clear(chatId);

            return BotPartReply.builder()
                    .nextCommand(CommandType.SAVE_CARD_SET_RESPONSE)
                    .previousProcessableMessage(processableMessage)
//                    .messageId(processableMessage.getMessageId())
                    .chatId(chatId)
                    .build();
        } else {
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
            action = () -> apiCallService.updateCardSet(cardSetDto);
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
