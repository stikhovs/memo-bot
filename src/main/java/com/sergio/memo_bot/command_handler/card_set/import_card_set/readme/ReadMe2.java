package com.sergio.memo_bot.command_handler.card_set.import_card_set.readme;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.BotImageReply;
import com.sergio.memo_bot.util.MarkUpUtil;
import com.sergio.memo_bot.util.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReadMe2 implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.IMPORT_CARD_SET_README_2 == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        InputStream img = getClass().getClassLoader().getResourceAsStream("images/import/import_card_set_readme_2.png");
        return BotImageReply.builder()
                .chatId(processableMessage.getChatId())
                .image(img)
                .fileName("import_readme_2")
                .caption("Шаг 2")
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of("Назад", CommandType.IMPORT_CARD_SET_README_1),
                        Pair.of("Шаг 3", CommandType.IMPORT_CARD_SET_README_3)
                )))
                .build();
    }
}