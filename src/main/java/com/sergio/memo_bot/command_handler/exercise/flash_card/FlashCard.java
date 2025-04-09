package com.sergio.memo_bot.command_handler.exercise.flash_card;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.FlashCardData;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.VisibleSide;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotMessageReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

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

        String text = data.getVisibleSide() == VisibleSide.FRONT ? data.getCard().getFrontSide() : data.getCard().getBackSide();


        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(text)
                .replyMarkup(
                        InlineKeyboardMarkup.builder()
                                .keyboard(List.of(
                                        new InlineKeyboardRow(defineBackAndNextButtons(data)),
                                        new InlineKeyboardRow(
                                                InlineKeyboardButton.builder()
                                                        .text("К списку упражнений")
                                                        .callbackData(CommandType.GET_EXERCISES.getCommandText())
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
            buttons.add(getFlipButton(data));
        }

        if (data.getCurrentIndex() > 0) {
            buttons.addFirst(getBackButton(data));
        }

        buttons.add(getFlipButton(data));

        if (data.getCurrentIndex() < data.getTotalNumberOfCards() - 1) {
            buttons.addLast(getNextButton(data));
        }

        return buttons;
    }

    private InlineKeyboardButton getFlipButton(FlashCardData data) {
        return InlineKeyboardButton.builder().text("Перевернуть").callbackData(CommandType.FLIP_REQUEST.getCommandText()).build();
    }

    private InlineKeyboardButton getNextButton(FlashCardData data) {
        return InlineKeyboardButton.builder().text("Далее").callbackData(CommandType.NEXT_FLASH_CARD_REQUEST.getCommandText()).build();
    }

    private InlineKeyboardButton getBackButton(FlashCardData data) {
        return InlineKeyboardButton.builder().text("Назад").callbackData(CommandType.PREVIOUS_FLASH_CARD_REQUEST.getCommandText()).build();
    }
}
