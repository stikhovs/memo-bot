package com.sergio.memo_bot.persistence.repository;

import com.sergio.memo_bot.persistence.entity.ChatTempData;
import com.sergio.memo_bot.state.CommandType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatTempDataRepository extends ListCrudRepository<ChatTempData, Long> {

    Optional<ChatTempData> findOneByChatIdAndCommand(Long chatId, CommandType command);

    @Transactional
    @Modifying
    @Query("""
            delete from ChatTempData ctd
            where ctd.chatId = :chatId
            """)
    void deleteByChatId(Long chatId);

    @Transactional
    @Modifying
    @Query("""
            delete from ChatTempData ctd
            where ctd.chatId = :chatId
            and ctd.command = :command
            """)
    void deleteByChatIdAndCommand(Long chatId, CommandType command);

}
