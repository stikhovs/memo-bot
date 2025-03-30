package com.sergio.memo_bot.persistence.service;

import com.sergio.memo_bot.persistence.entity.AwaitsUserInput;
import com.sergio.memo_bot.persistence.repository.ChatAwaitsInputRepository;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAwaitsInputService {

    private final ChatAwaitsInputRepository chatAwaitsInputRepository;

    public void clear(Long chatId) {
        chatAwaitsInputRepository.deleteByChatId(chatId);
    }
    public void save(Long chatId, CommandType nextCommand) {
        chatAwaitsInputRepository.save(AwaitsUserInput.builder()
                .chatId(chatId)
                .inputType("TEXT")
                .nextCommand(nextCommand.getCommandText())
                .build());
    }

    @Transactional
    public void clearAndSave(Long chatId, CommandType nextCommand) {
        clear(chatId);
        save(chatId, nextCommand);
    }

    public AwaitsUserInput getByChatId(Long chatId) {
        return chatAwaitsInputRepository.findOneByChatId(chatId)
                .orElseThrow(() -> new RuntimeException("Must find the record by chatId: %s".formatted(chatId)));
    }

    @Transactional
    public void update(Long chatId, CommandType nextCommand) {
        AwaitsUserInput awaitsUserInput = chatAwaitsInputRepository.findOneByChatId(chatId)
                .map(aui -> aui.toBuilder()
                        .inputType("TEXT")
                        .nextCommand(nextCommand.getCommandText())
                        .chatId(chatId)
                        .build())
                .orElseThrow(() -> new RuntimeException("Must find the record by chatId"));

        chatAwaitsInputRepository.save(awaitsUserInput);
    }

    public List<AwaitsUserInput> findAll(Long chatId) {
        return chatAwaitsInputRepository.findIfAwaitsUserTextInput(chatId);
    }

}
