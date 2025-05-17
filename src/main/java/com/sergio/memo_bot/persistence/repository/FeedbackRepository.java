package com.sergio.memo_bot.persistence.repository;

import com.sergio.memo_bot.persistence.entity.feedback.Feedback;
import com.sergio.memo_bot.persistence.entity.feedback.FeedbackStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends ListCrudRepository<Feedback, Long> {

    Optional<Feedback> findByMessageId(Integer messageId);

    @Transactional
    @Modifying
    @Query("""
            delete from Feedback f
            where f.status = :status
            """)
    void deleteByStatus(FeedbackStatus status);
}
