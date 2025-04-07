package com.sergio.memo_bot.command_handler.card_set_manipulation;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCards implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_CARDS == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {

        CardSetDto cardSetDto = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.GET_CARD_SET_INFO, CardSetDto.class);

        return BotMessageReply.builder()
//                .type(BotReplyType.EDIT_MESSAGE_TEXT)
                .chatId(processableMessage.getChatId())
                .parseMode(ParseMode.HTML)
                .text(
                        "<strong><u>%s</u></strong>\n\n".formatted(cardSetDto.getTitle())
                        + cardSetDto.getCards().stream().map(cardDto -> cardDto.getFrontSide() + " — " + cardDto.getBackSide()).collect(Collectors.joining("\n"))
                )
//                .messageId(processableMessage.getMessageId())
                .replyMarkup(
                        InlineKeyboardMarkup.builder()
                                .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                        .text("Назад")
                                        .callbackData(CommandType.GET_CARD_SET_INFO.getCommandText().formatted(cardSetDto.getId()))
                                        .build()))
                                .build()
                )
                .build();
    }
}
