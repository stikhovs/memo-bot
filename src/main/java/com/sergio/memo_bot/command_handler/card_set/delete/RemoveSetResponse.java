package com.sergio.memo_bot.command_handler.card_set.delete;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.external.http.card_set.CardSetHttpService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CARD_SET_IS_DELETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveSetResponse implements CommandHandler {

    private final CardSetHttpService cardSetHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.REMOVE_SET_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        CardSetDto cardSet = chatTempDataService.mapDataToType(chatId, CommandType.GET_CARD_SET_INFO, CardSetDto.class);
        cardSetHttpService.deleteCardSet(cardSet.getId());

        chatTempDataService.clear(chatId, CommandType.GET_CARD_SET_INFO);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CARD_SET_IS_DELETED)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.MAIN_MENU)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();
    }
}
