package com.sergio.memo_bot.command_handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotReply;
import com.sergio.memo_bot.util.BotReplyType;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCardSetInfo implements CommandHandler {

    private final ChatTempDataRepository chatTempDataRepository;
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.GET_CARD_SET_INFO == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        String cardSetId = processableMessage.getText().split("__")[1];

        List<ChatTempData> tempData = chatTempDataRepository.findByChatId(processableMessage.getChatId());
        if (CollectionUtils.isEmpty(tempData)) {
            throw new RuntimeException("Temp data should not be empty on this step");
        }

        List<CardSetDto> cardSets = new Gson().fromJson(tempData.getFirst().getData(), new TypeToken<List<CardSetDto>>() {}.getType());

        String title = cardSets.stream()
                .filter(cardSetDto -> cardSetDto.getUuid().equals(UUID.fromString(cardSetId)))
                .map(CardSetDto::getTitle)
                .findFirst().orElseThrow();

        return BotReply.builder()
                .type(BotReplyType.EDIT_MESSAGE_TEXT)
                .chatId(processableMessage.getChatId())
                .messageId(processableMessage.getMessageId())
                .text(title)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(Pair.of("main menu", CommandType.MAIN_MENU))))
                .build();
    }
}
