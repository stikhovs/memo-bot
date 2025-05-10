package com.sergio.memo_bot.command_handler.card_set.import_card_set.quizlet.readme;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.reply.BotImageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;

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
                .caption(IMPORT_README_1)
                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                        Pair.of(BACK, CommandType.IMPORT_CARD_SET_FROM_QUIZLET_MENU),
                        Pair.of(IMPORT_README_2_BTN_TEXT, CommandType.IMPORT_CARD_SET_README_2)
                )))
                .build();
    }
}