package com.sergio.memo_bot.command_handler.exercise.connect_words;

import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.ConnectWordsData;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.ConnectedPair;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.WordWithHiddenAnswer;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotMessageReply;
import com.sergio.memo_bot.reply.NextReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import com.sergio.memo_bot.util.MarkUpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sergio.memo_bot.reply_text.ReplyTextConstant.*;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectWordsRequest implements CommandHandler {

    private static final int MAX_NUMBER_OF_BUTTONS_IN_ONE_ROW = 4;
    private static final int MAX_NUMBER_OF_SYMBOLS_IN_ONE_ROW = 24;

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CONNECT_WORDS_REQUEST == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        Long chatId = processableMessage.getChatId();
        ConnectWordsData connectWordsData = chatTempDataService.mapDataToType(chatId, CommandType.CONNECT_WORDS_REQUEST, ConnectWordsData.class);

        String text;

        List<WordWithHiddenAnswer> currentButtons;
        Optional<WordWithHiddenAnswer> activePair = connectWordsData.getWordsWithHiddenAnswers().stream().filter(WordWithHiddenAnswer::isActive).findFirst();


        if (activePair.isEmpty() && isEmpty(connectWordsData.getConnectedPairs())) {
            currentButtons = connectWordsData.getWordsWithHiddenAnswers();
            text = FIND_ALL_PAIRS;
        } else {
            List<ConnectedPair> correctAnswers = connectWordsData.getConnectedPairs();

            currentButtons = connectWordsData.getWordsWithHiddenAnswers().stream()
                    .filter(wordWithHiddenAnswer -> correctAnswers.stream()
                            .noneMatch(
                                    it -> it.getWordOneId().equals(wordWithHiddenAnswer.getId())
                                            || it.getWordTwoId().equals(wordWithHiddenAnswer.getId())
                            ))
                    .toList();

            if (activePair.isPresent()) {
                if (isNotEmpty(correctAnswers)) {
                    String correctStr = getCorrectStr(correctAnswers);

                    text = FIND_ALL_PAIRS_1.formatted(activePair.get().getWordToShow(), connectWordsData.getMistakeCount(), correctStr);
                } else {
                    text = FIND_ALL_PAIRS_2.formatted(activePair.get().getWordToShow(), connectWordsData.getMistakeCount());
                }
            } else if (isNotEmpty(connectWordsData.getWordsWithHiddenAnswers())) {
                String correctStr = getCorrectStr(correctAnswers);
                text = FIND_ALL_PAIRS_3.formatted(connectWordsData.getMistakeCount(), correctStr);
            } else {

                if (connectWordsData.isPageable()) {
                    if (connectWordsData.getCurrentPage() + 1 == connectWordsData.getTotalNumberOfPages()) {
                        // if it was the last page
                        chatTempDataService.clear(chatId, CommandType.CONNECT_WORDS_REQUEST);
                        return BotMessageReply.builder()
                                .chatId(chatId)
                                .text(EXERCISE_FINISHED)
                                .parseMode(ParseMode.HTML)
                                .nextReply(NextReply.builder()
                                        .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                                        .previousProcessableMessage(processableMessage)
                                        .build())
                                .build();
                    } else {
                        return BotMessageReply.builder()
                                .chatId(chatId)
                                .text(LEVEL_FINISHED.formatted(connectWordsData.getCurrentPage() + 1, connectWordsData.getTotalNumberOfPages()))
                                .replyMarkup(MarkUpUtil.getInlineKeyboardMarkup(List.of(
                                        org.apache.commons.lang3.tuple.Pair.of(LEAVE_LEVEL, CommandType.EXERCISES_DATA_PREPARE),
                                        org.apache.commons.lang3.tuple.Pair.of(NEXT_LEVEL, CommandType.CONNECT_WORDS_PREPARE)
                                )))
                                .build();
                    }
                } else {

                    return BotMessageReply.builder()
                            .chatId(chatId)
                            .text(EXERCISE_FINISHED)
                            .parseMode(ParseMode.HTML)
                            .nextReply(NextReply.builder()
                                    .nextCommand(CommandType.EXERCISES_DATA_PREPARE)
                                    .previousProcessableMessage(processableMessage)
                                    .build())
                            .build();
                }
            }
        }

        return BotMessageReply.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(getButtons(currentButtons))
                        .build())
                .build();
    }

    private String getCorrectStr(List<ConnectedPair> correctAnswers) {
        return correctAnswers.stream()
                .map(connectedPair -> connectedPair.getWordOne() + " — " + connectedPair.getWordTwo())
                .collect(Collectors.joining("\n     "));
    }

    private List<InlineKeyboardRow> getButtons(List<WordWithHiddenAnswer> currentButtons) {
        List<InlineKeyboardRow> rows = new ArrayList<>();

        List<InlineKeyboardButton> buttons = currentButtons.stream()
                .map(btn -> InlineKeyboardButton.builder()
                        .text(btn.isActive()
                                ? "| " + btn.getWordToShow() + " |"
                                : btn.getWordToShow()
                        )
                        .callbackData(CommandType.CONNECT_WORDS_RESPONSE.getCommandText().formatted(btn.getId()))
                        .build())
                .collect(Collectors.toList());

        List<InlineKeyboardButton> currentRow = new ArrayList<>();
        int currentRowSymbols = 0;

        for (InlineKeyboardButton button : buttons) {
            int btnLength = button.getText().length();

            // если следующая кнопка не влезает по длине или по количеству
            if (currentRow.size() >= MAX_NUMBER_OF_BUTTONS_IN_ONE_ROW ||
                    currentRowSymbols + btnLength > MAX_NUMBER_OF_SYMBOLS_IN_ONE_ROW) {

                // сохранить строку
                if (!currentRow.isEmpty()) {
                    rows.add(new InlineKeyboardRow(currentRow));
                    currentRow = new ArrayList<>();
                    currentRowSymbols = 0;
                }
            }

            currentRow.add(button);
            currentRowSymbols += btnLength;
        }

        // добавить остатки
        if (!currentRow.isEmpty()) {
            rows.add(new InlineKeyboardRow(currentRow));
        }

        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text(BACK)
                .callbackData(CommandType.EXERCISES_DATA_PREPARE.getCommandText())
                .build()));

        return rows;
    }

}