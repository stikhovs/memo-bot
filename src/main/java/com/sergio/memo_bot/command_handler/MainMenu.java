package com.sergio.memo_bot.command_handler;

import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatAwaitsInputService;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainMenu implements CommandHandler {

    private final ChatAwaitsInputService chatAwaitsInputService;
    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.MAIN_MENU == commandType;
    }

    @Override
    @Transactional
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();

        chatAwaitsInputService.clear(chatId);
        chatTempDataService.clear(chatId);

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(MAIN_MENU)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkupRows(List.of(
                        Pair.of(CARD_SETS, CommandType.CARD_SET_MENU_DATA),
                        Pair.of(CATEGORIES, CommandType.CATEGORY_MENU_DATA),
                        Pair.of(EXERCISES, CommandType.EXERCISES_FROM_MAIN_MENU)
                )))
                .parseMode(ParseMode.HTML)
                .build();
    }

}
