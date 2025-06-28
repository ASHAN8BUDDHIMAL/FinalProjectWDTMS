package com.example.demo.repository;

import com.example.demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    // Existing conversation query
    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderFromId = :userId1 AND m.senderToId = :userId2) OR " +
            "(m.senderFromId = :userId2 AND m.senderToId = :userId1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // New method to get all messages from specific sender to specific recipient
    @Query("SELECT m FROM Message m WHERE " +
            "m.senderFromId = :senderId AND m.senderToId = :recipientId " +
            "ORDER BY m.sentAt ASC")
    List<Message> findAllMessagesBetweenUsers(
            @Param("senderId") Long senderId,
            @Param("recipientId") Long recipientId);

    // Existing methods...
    List<Message> findBySenderToIdAndIsRead(Long senderToId, boolean isRead);

    List<Message> findBySenderFromIdOrSenderToId(Long senderFromId, Long senderToId);
}