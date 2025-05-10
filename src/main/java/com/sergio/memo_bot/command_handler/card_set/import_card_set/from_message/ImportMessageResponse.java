package com.sergio.memo_bot.command_handler.card_set.import_card_set.from_message;

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

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CARD_SET_SUCCESSFULLY_IMPORTED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportMessageResponse implements CommandHandler {


    private final CardSetHttpService cardSetHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_MESSAGE_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        Long categoryId = Long.valueOf(chatTempDataService.get(chatId, CommandType.CHOOSE_CATEGORY_FOR_MESSAGE_IMPORT_RESPONSE).getData());
        CardSetDto cardSetDto = chatTempDataService.mapDataToType(chatId, CommandType.IMPORT_MESSAGE_RESPONSE, CardSetDto.class);

        cardSetHttpService.saveCardSet(cardSetDto.toBuilder()
                .telegramChatId(chatId)
                .categoryId(categoryId)
                .build()
        );

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CARD_SET_SUCCESSFULLY_IMPORTED)
                .nextReply(NextReply.builder()
                        .nextCommand(CommandType.MAIN_MENU)
                        .previousProcessableMessage(processableMessage)
                        .build())
                .build();
    }
}