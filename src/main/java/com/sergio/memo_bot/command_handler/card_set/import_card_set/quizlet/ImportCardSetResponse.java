package com.sergio.memo_bot.command_handler.card_set.import_card_set.quizlet;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
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

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.CARD_SET_SUCCESSFULLY_IMPORTED;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportCardSetResponse implements CommandHandler {

    private final CardSetHttpService cardSetHttpService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_CARD_SET_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        String title = chatTempDataService.get(chatId, CommandType.IMPORT_CARD_SET_TITLE_RESPONSE).getData();
        Long categoryId = Long.valueOf(chatTempDataService.get(chatId, CommandType.CHOOSE_CATEGORY_FOR_IMPORT_RESPONSE).getData());
        List<CardDto> cards = chatTempDataService.mapDataToList(chatId, CommandType.IMPORT_CARDS_RESPONSE, CardDto.class);

        CardSetDto cardSetDto = CardSetDto.builder()
                .telegramChatId(chatId)
                .categoryId(categoryId)
                .title(title)
                .cards(cards)
                .build();

        cardSetHttpService.saveCardSet(cardSetDto);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(CARD_SET_SUCCESSFULLY_IMPORTED)
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.CARD_SET_MENU_DATA)
                        .build())
                .build();
    }
}