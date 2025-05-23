package com.sergio.memo_bot.persistence.repository;

import com.sergio.memo_bot.persistence.entity.ChatMessage;
import com.sergio.memo_bot.persistence.entity.SenderType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends ListCrudRepository<ChatMessage, Long> {

    Optional<ChatMessage> findOneByChatIdAndMessageId(Long chatId, Integer messageId);

    List<ChatMessage> findByChatIdOrderByUpdatedAtDesc(Long chatId);

    List<ChatMessage> findByCreatedAtBefore(LocalDateTime threshold);

    List<ChatMessage> findByChatIdAndSenderTypeOrderByUpdatedAtDesc(Long chatId, SenderType senderType);

    Optional<ChatMessage> findFirstByChatIdAndSenderTypeOrderByUpdatedAtDesc(Long chatId, SenderType senderType);

    Optional<ChatMessage> findFirstByChatIdAndHasButtonsIsTrueOrderByUpdatedAtDesc(Long chatId);

    Optional<ChatMessage> findFirstByChatIdOrderByUpdatedAtDesc(Long chatId);

    @Transactional
    @Modifying
    @Query("""
            delete from ChatMessage cm
            where cm.chatId = :chatId
            """)
    void deleteByChatId(Long chatId);

    @Transactional
    @Modifying
    @Query("""
            delete from ChatMessage cm
            where cm.chatId = :chatId
            and cm.messageId in (:messageIds)
            """)
    void deleteByChatIdAndMessages(Long chatId, List<Integer> messageIds);

}
