package com.sergio.memo_bot.persistence.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sergio.memo_bot.dto.CardSetDto;
import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.persistence.repository.ChatTempDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatTempDataService {

    private final ChatTempDataRepository chatTempDataRepository;


    public void clear(Long chatId) {
        chatTempDataRepository.deleteByChatId(chatId);
    }

    public ChatTempData save (ChatTempData chatTempData) {
        return chatTempDataRepository.save(chatTempData);
    }

    @Transactional
    public ChatTempData clearAndSave(Long chatId, ChatTempData chatTempData) {
        clear(chatId);
        return save(chatTempData);
    }

    public ChatTempData get(Long chatId) {
        List<ChatTempData> tempDataList = chatTempDataRepository.findByChatId(chatId);
        if (CollectionUtils.isEmpty(tempDataList)) {
            throw new RuntimeException("Temp data should not be empty on this step");
        }
        return tempDataList.getFirst();
    }

    public <T> T mapDataToType(Long chatId, Class<T> resultType) {
        ChatTempData chatTempData = get(chatId);
        return new Gson().fromJson(chatTempData.getData(), resultType);
    }

    public <T> List<T> mapDataToList(Long chatId, Class<T> elementType) {
        ChatTempData chatTempData = get(chatId);
        return new Gson().fromJson(chatTempData.getData(), TypeToken.getParameterized(List.class, elementType).getType());
    }

}
