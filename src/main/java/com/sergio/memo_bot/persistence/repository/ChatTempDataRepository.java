package com.sergio.memo_bot.persistence.repository;

import com.sergio.memo_bot.persistence.entity.ChatTempData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatTempDataRepository extends ListCrudRepository<ChatTempData, Long> {

    List<ChatTempData> findByChatId(Long chatId);

    @Transactional
    @Modifying
    @Query("""
            delete from ChatTempData ctd
            where ctd.chatId = :chatId
            """)
    void deleteByChatId(Long chatId);

}
