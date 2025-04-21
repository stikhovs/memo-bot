package com.sergio.memo_bot.command_handler.card_set.import_card_set.readme;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotImageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReadMe1 implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_CARD_SET_README_1 == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        InputStream img = getClass().getClassLoader().getResourceAsStream("images/import/import_card_set_readme_1.png");
        return BotImageReply.builder()
                .chatId(processableMessage.getChatId())
                .image(img)
                .fileName("import_readme_1")
                .caption("Шаг 1")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of("Назад", CommandType.IMPORT_CARD_SET_MENU),
                        Pair.of("Шаг 2", CommandType.IMPORT_CARD_SET_README_2)
                )))
                .build();
    }
}