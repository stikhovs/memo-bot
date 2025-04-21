package com.sergio.memo_bot.command_handler.exercise.connect_words;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.ConnectWordsData;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.ConnectedPair;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.WordWithHiddenAnswer;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectWordsRequest implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CONNECT_WORDS_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        ConnectWordsData connectWordsData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.CONNECT_WORDS_REQUEST, ConnectWordsData.class);

        String text;

        List<WordWithHiddenAnswer> currentButtons;
        Optional<WordWithHiddenAnswer> activePair = connectWordsData.getWordsWithHiddenAnswers().stream().filter(WordWithHiddenAnswer::isActive).findFirst();


        if (activePair.isEmpty() && isEmpty(connectWordsData.getConnectedPairs())) {
            currentButtons = connectWordsData.getWordsWithHiddenAnswers();
            text = "Найдите все пары";
        } else {
            List<ConnectedPair> correctAnswers = connectWordsData.getConnectedPairs();

            currentButtons = connectWordsData.getWordsWithHiddenAnswers().stream()
                    .filter(wordWithHiddenAnswer -> correctAnswers.stream()
                            .noneMatch(
                                    it -> it.getWordOne().equals(wordWithHiddenAnswer.getWordToShow())
                                            || it.getWordTwo().equals(wordWithHiddenAnswer.getWordToShow())
                            ))
                    .filter(wordWithHiddenAnswer -> !wordWithHiddenAnswer.isActive())
                    .toList();

            if (activePair.isPresent()) {
                if (isNotEmpty(correctAnswers)) {
                    String correctStr = getCorrectStr(correctAnswers);

                    text = "Найдите все пары \n\nСейчас ищем пару для: %s \n\nКоличество ошибок: %s \n\nНайденные пары: \n\n%s".formatted(activePair.get().getWordToShow(), connectWordsData.getMistakeCount(), correctStr);
                } else {
                    text = "Найдите все пары \n\nСейчас ищем пару для: %s\n\nКоличество ошибок: %s \n\n".formatted(activePair.get().getWordToShow(), connectWordsData.getMistakeCount());
                }
            } else if (isNotEmpty(connectWordsData.getWordsWithHiddenAnswers())) {
                String correctStr = getCorrectStr(correctAnswers);
                text = "Найдите все пары \n\nКоличество ошибок: %s \n\nНайденные пары: \n\n%s".formatted(connectWordsData.getMistakeCount(), correctStr);
            } else {
                return BotMessageReply.builder()
                        .chatId(processableMessage.getChatId())
                        .text("Завершено!")
                        .nextReply(NextReply.builder()
                                .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                                .previousProcessableMessage(processableMessage)
                                .build())
                        .build();
            }
        }

        return BotMessageReply.builder()
                .chatId(processableMessage.getChatId())
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(
                                new InlineKeyboardRow(
                                        currentButtons.stream()
                                                .map(btn -> InlineKeyboardButton.builder().text(btn.getWordToShow()).callbackData(CommandType.CONNECT_WORDS_RESPONSE.getCommandText().formatted(btn.getId())).build())
                                                .limit(currentButtons.size() / 2)
                                                .toList()
                                ),
                                new InlineKeyboardRow(
                                        currentButtons.stream()
                                                .map(btn -> InlineKeyboardButton.builder().text(btn.getWordToShow()).callbackData(CommandType.CONNECT_WORDS_RESPONSE.getCommandText().formatted(btn.getId())).build())
                                                .skip(currentButtons.size() / 2)
                                                .toList()
                                ),
                                new InlineKeyboardRow(InlineKeyboardButton.builder().text("Назад").callbackData(CommandType.EXERCISES_DATA_PREPARE.getCommandText()).build())
                                ))
                        .build())
                .build();
    }

    private String getCorrectStr(List<ConnectedPair> correctAnswers) {
        return correctAnswers.stream()
                .map(connectedPair -> connectedPair.getWordOne() + " — " + connectedPair.getWordTwo())
                .collect(Collectors.joining("\n"));
    }

}