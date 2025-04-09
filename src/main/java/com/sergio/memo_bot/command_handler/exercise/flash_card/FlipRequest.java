package com.sergio.memo_bot.command_handler.exercise.flash_card;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.FlashCardData;
import com.sergio.memo_bot.command_handler.exercise.flash_card.dto.VisibleSide;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotPartReply;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlipRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.FLIP_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        FlashCardData data = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.FLASH_CARD, FlashCardData.class);

        VisibleSide flipped = flipVisibleSide(data);
        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.FLASH_CARD)
                .data(new Gson().toJson(data.toBuilder()
                        .visibleSide(flipped)
                        .build()))
                .build());

        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .nextCommand(CommandType.FLASH_CARD)
                .previousProcessableMessage(processableMessage)
                .build();
    }

    private VisibleSide flipVisibleSide(FlashCardData data) {
        return switch (data.getVisibleSide()) {
            case FRONT -> VisibleSide.BACK;
            case BACK -> VisibleSide.FRONT;
        };
    }
}
