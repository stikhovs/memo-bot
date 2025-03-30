package com.sergio.memo_bot.update_handler.text;

import com.sergio.memo_bot.update_handler.UpdateHandler;
import com.sergio.memo_bot.update_handler.text.path.TextPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextHandler implements UpdateHandler {

    private final List<TextPath> paths;
    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }
    @Override
    public BotApiMethodMessage handle(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();

        TextPath path = paths.stream()
                .filter(textPath -> textPath.canProcess(msg))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Couldn't find the right path for message: [%s]".formatted(msg.getText())));

        return path.process(msg, chatId);
    }

    /*@SneakyThrows
    public SendPoll sendPoll(Long who) {
        return SendPoll.builder()
                .chatId(who)
                .type("quiz") // "regular" or "quiz"
                .question("This is question")
                .options(List.of("option 1", "option 2", "option 3"))
                .correctOptionId(0)
                .protectContent(true)
                .isAnonymous(false)
                .build();
    }*/

    /*
    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }*/
}
