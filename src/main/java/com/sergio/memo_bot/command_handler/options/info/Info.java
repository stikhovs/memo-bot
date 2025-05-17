package com.sergio.memo_bot.command_handler.options.info;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.reply.BotImageReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;

import java.io.InputStream;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.BOT_INFO_TEXT;

@Slf4j
@Component
@RequiredArgsConstructor
public class Info implements CommandHandler {
    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.INFO == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        InputStream img = getClass().getClassLoader().getResourceAsStream("images/structure/Memorika diagram rus.png");

        return BotImageReply.builder()
                .chatId(processableMessage.getChatId())
                .image(img)
                .fileName("Memorika")
                .caption(BOT_INFO_TEXT)
                .parseMode(ParseMode.HTML)
                .build();
    }
}