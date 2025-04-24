package com.sergio.memo_bot.command_handler.card_set.import_card_set;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.CardDto;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.STRING_IS_TOO_LONG;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportCardsResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;
    private final ChatAwaitsInputService chatAwaitsInputService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_CARDS_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);

        String data = processableMessage.getText();

        if (isValid(data)) {
            List<CardDto> cards = data.trim().lines()
                    .filter(this::validLine)
                    .map(card -> {
                        String[] split = card.trim().split("  ");
                        return CardDto.builder()
                                .frontSide(split[0].trim())
                                .backSide(split[1].trim())
                                .build();
                    })
                    .toList();

            if (isEmpty(cards)) {
                return getErrorMessage(processableMessage);
            }

            chatTempDataService.clearAndSave(chatId,
                    ChatTempData.builder()
                            .chatId(chatId)
                            .data(new Gson().toJson(cards))
                            .command(CommandType.IMPORT_CARDS_RESPONSE)
                            .build()
            );

            return BotPartReply.builder()
                    .chatId(chatId)
                    .previousProcessableMessage(processableMessage)
                    .nextCommand(CommandType.IMPORT_CARD_SET_RESPONSE)
                    .build();
        } else {
            return getErrorMessage(processableMessage);
        }
    }

    private boolean validLine(String card) {
        String[] split = card.trim().split("  ");
        return ArrayUtils.getLength(split) == 2 && wordIsLessThan100(split[0]) && wordIsLessThan100(split[1]);
    }

    private boolean wordIsLessThan100(String word) {
        if (StringUtils.length(word) < 100) {
            log.warn(STRING_IS_TOO_LONG);
            return false;
        }
        return true;
    }

    private boolean isValid(String data) {
        return StringUtils.isNotBlank(data);
    }

    private Reply getErrorMessage(ProcessableMessage processableMessage) {
        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text("Пожалуйста, попробуйте снова")
                .nextReply(NextReply.builder()
                        .previousProcessableMessage(processableMessage)
                        .nextCommand(CommandType.IMPORT_CARDS_REQUEST)
                        .build())
                .build();
    }
}