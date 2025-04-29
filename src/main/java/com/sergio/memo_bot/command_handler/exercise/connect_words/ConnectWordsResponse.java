package com.sergio.memo_bot.command_handler.exercise.connect_words;

import com.google.gson.Gson;
import com.sergio.memo_bot.command_handler.CommandHandler;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.ConnectWordsData;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.ConnectedPair;
import com.sergio.memo_bot.command_handler.exercise.connect_words.dto.WordWithHiddenAnswer;
import com.sergio.memo_bot.dto.ProcessableMessage;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.service.ChatTempDataService;
import com.sergio.memo_bot.reply.BotPartReply;
import com.sergio.memo_bot.reply.Reply;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectWordsResponse implements CommandHandler {

    private final ChatTempDataService chatTempDataService;

    @Override
    public boolean canHandle(CommandType commandType) {
        return CommandType.CONNECT_WORDS_RESPONSE == commandType;
    }

    @Override
    public Reply getReply(ProcessableMessage processableMessage) {
        ConnectWordsData connectWordsData = chatTempDataService.mapDataToType(processableMessage.getChatId(), CommandType.CONNECT_WORDS_REQUEST, ConnectWordsData.class);
        String[] commandAndBtnId = processableMessage.getText().split("__");

        int btnId = NumberUtils.parseNumber(commandAndBtnId[1], Integer.class);

        WordWithHiddenAnswer chosenWord = connectWordsData.getWordsWithHiddenAnswers().stream().filter(wordWithHiddenAnswer -> wordWithHiddenAnswer.getId() == btnId)
                .findFirst().get();

        Optional<WordWithHiddenAnswer> activePair = connectWordsData.getWordsWithHiddenAnswers().stream().filter(WordWithHiddenAnswer::isActive).findFirst();

        if (activePair.isPresent()) {
            boolean isTheSame = activePair.get().getId() == chosenWord.getId();

            if (isTheSame) {
                chosenWord.setActive(false);
            } else {
                boolean isCorrect = activePair.get().getHiddenAnswer().equals(chosenWord.getWordToShow());

                if (isCorrect) {
                    connectWordsData.getConnectedPairs().add(ConnectedPair.builder()
                            .wordOne(activePair.get().getWordToShow())
                            .wordOneId(activePair.get().getId())
                            .wordTwo(chosenWord.getWordToShow())
                            .wordTwoId(chosenWord.getId())
                            .build());
                    connectWordsData.setWordsWithHiddenAnswers(
                            connectWordsData.getWordsWithHiddenAnswers().stream()
                                    .filter(it -> !it.isActive())
                                    .filter(it -> it.getId() != btnId)
                                    .toList()
                    );
                } else {
                    connectWordsData.setMistakeCount(connectWordsData.getMistakeCount() + 1);
                }
            }

        } else {
            connectWordsData.setWordsWithHiddenAnswers(connectWordsData.getWordsWithHiddenAnswers().stream().peek(it -> {
                if (it.getId() == btnId) {
                    it.setActive(true);
                }
            }).toList());
        }

        chatTempDataService.clearAndSave(processableMessage.getChatId(), ChatTempData.builder()
                .chatId(processableMessage.getChatId())
                .command(CommandType.CONNECT_WORDS_REQUEST)
                .data(new Gson().toJson(connectWordsData))
                .build());

        return BotPartReply.builder()
                .chatId(processableMessage.getChatId())
                .previousProcessableMessage(processableMessage)
                .nextCommand(CommandType.CONNECT_WORDS_REQUEST)
                .build();
    }

}