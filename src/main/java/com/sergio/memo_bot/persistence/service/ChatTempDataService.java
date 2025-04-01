package com.sergio.memo_bot.persistence.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import com.sergio.memo_bot.state.CommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatTempDataService {

    private final ChatTempDataRepository chatTempDataRepository;


    public void clear(Long chatId) {
        chatTempDataRepository.deleteByChatId(chatId);
    }

    public void clear(Long chatId, CommandType commandType) {
        chatTempDataRepository.deleteByChatIdAndCommand(chatId, commandType);
    }

    public ChatTempData save (ChatTempData chatTempData) {
        return chatTempDataRepository.save(chatTempData);
    }

    /*@Transactional
    public ChatTempData clearAndSave(Long chatId, ChatTempData chatTempData) {
        clear(chatId);
        return save(chatTempData);
    }*/
    @Transactional
    public ChatTempData clearAndSave(Long chatId, ChatTempData chatTempData) {
        clear(chatId, chatTempData.getCommand());
        return save(chatTempData);
    }

    public ChatTempData get(Long chatId, CommandType commandType) {
        return chatTempDataRepository.findOneByChatIdAndCommand(chatId, commandType)
                .orElseThrow(() -> new RuntimeException("Temp data should not be empty on this step"));
    }

    public Optional<ChatTempData> find(Long chatId, CommandType commandType) {
        return chatTempDataRepository.findOneByChatIdAndCommand(chatId, commandType);
    }

    public <T> T mapDataToType(Long chatId, CommandType commandType, Class<T> resultType) {
        ChatTempData chatTempData = get(chatId, commandType);
        return new Gson().fromJson(chatTempData.getData(), resultType);
    }

    public <T> List<T> mapDataToList(Long chatId, CommandType commandType, Class<T> elementType) {
        ChatTempData chatTempData = get(chatId, commandType);
        return new Gson().fromJson(chatTempData.getData(), TypeToken.getParameterized(List.class, elementType).getType());
    }

}
