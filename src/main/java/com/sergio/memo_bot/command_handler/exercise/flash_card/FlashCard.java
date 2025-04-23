package com.sergio.memo_bot.command_handler.exercise.flash_card;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.FlashCardData;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.VisibleSide;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlashCard implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.FLASH_CARD == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        FlashCardData data = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.FLASH_CARD, FlashCardData.class);

        String text = data.getVisibleSide() == VisibleSide.FRONT
                ? FLACH_CARD_FRONT_SIDE + "<strong>" + data.getCard().getFrontSide() + "</strong>"
                : FLACH_CARD_BACK_SIDE + "<strong>" + data.getCard().getBackSide() + "</strong>";

        String currentNumber = "\n\n\n\n<i><u>%s/%s</u></i>".formatted(data.getCurrentIndex()+1, data.getTotalNumberOfCards());


        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(text + currentNumber)
                .parseMode(ParseMode.HTML)
                .replyMarkup(
                        InlineKeyboardMarkup.builder()
                                .keyboard(List.of(
                                        new InlineKeyboardRow(getFlipButton()),
                                        new InlineKeyboardRow(defineBackAndNextButtons(data)),
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton.builder()
                                                        .text(TO_EXERCISES_LIST)
                                                        .callbackData(CommandType.EXERCISES_DATA_PREPARE.getCommandText())
                                                        .build()
                                        )
                                ))
                                .build()
                )
                .build();
    }

    private List<InlineKeyboardButton> defineBackAndNextButtons(FlashCardData data) {
        ArrayList<InlineKeyboardButton> buttons = new ArrayList<>();

        if (data.getCurrentIndex() == 0 && data.getTotalNumberOfCards() == 1) {
            return buttons;
        } else {

            if (data.getCurrentIndex() > 0) {
                buttons.addFirst(getBackButton());
            }

            if (data.getCurrentIndex() < data.getTotalNumberOfCards() - 1) {
                buttons.addLast(getNextButton());
            }

            return buttons;
        }
    }

    private InlineKeyboardButton getFlipButton() {
        return InlineKeyboardButton.builder().text(FLIP_CARD).callbackData(CommandType.FLIP_REQUEST.getCommandText()).build();
    }

    private InlineKeyboardButton getNextButton() {
        return InlineKeyboardButton.builder().text(NEXT_ARROW).callbackData(CommandType.NEXT_FLASH_CARD_REQUEST.getCommandText()).build();
    }

    private InlineKeyboardButton getBackButton() {
        return InlineKeyboardButton.builder().text(BACK_ARROW).callbackData(CommandType.PREVIOUS_FLASH_CARD_REQUEST.getCommandText()).build();
    }
}
