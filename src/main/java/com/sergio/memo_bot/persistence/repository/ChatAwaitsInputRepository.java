package com.sergio.memo_bot.persistence.repository;

import com.sergio.memo_bot.persistence.entity.AwaitsUserInput;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatAwaitsInputRepository extends ListCrudRepository<AwaitsUserInput, Long> {

    Optional<AwaitsUserInput> findOneByChatId(Long chatId);

    @Query("""
            select aui
            from AwaitsUserInput aui
            where aui.chatId = :chatId
                and aui.inputType = 'TEXT'
            order by aui.id desc
            """)
    List<AwaitsUserInput> findIfAwaitsUserTextInput(Long chatId);

    @Transactional
    @Modifying
    @Query("""
            delete from AwaitsUserInput aui
            where aui.chatId = :chatId
            """)
    void deleteByChatId(Long chatId);

}
