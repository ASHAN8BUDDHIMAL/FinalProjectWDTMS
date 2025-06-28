package com.example.demo.Service;

import com.example.demo.DTO.ConversationUserDTO;
import com.example.demo.DTO.MessageDTO;
import com.example.demo.model.Message;
import com.example.demo.model.UserRegistration;
import com.example.demo.repository.MessageRepo;
import com.example.demo.repository.RegUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private RegUser regUser;

    public Message saveMessage(Message message) {
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);
        return messageRepo.save(message);
    }

    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepo.findConversation(userId1, userId2);
    }

    public void markAsRead(Long messageId) {
        messageRepo.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            messageRepo.save(message);
        });
    }

    public List<Message> getUnreadMessages(Long userId) {
        return messageRepo.findBySenderToIdAndIsRead(userId, false);
    }

    public List<ConversationUserDTO> getConversationUsers(Long currentUserId) {
        List<Message> messages = messageRepo.findBySenderFromIdOrSenderToId(currentUserId, currentUserId);

        Set<Long> otherUserIds = new HashSet<>();
        for (Message msg : messages) {
            if (!msg.getSenderFromId().equals(currentUserId)) {
                otherUserIds.add(msg.getSenderFromId());
            }
            if (!msg.getSenderToId().equals(currentUserId)) {
                otherUserIds.add(msg.getSenderToId());
            }
        }

        List<UserRegistration> users = regUser.findAllById(otherUserIds);

        return users.stream()
                .map(u -> new ConversationUserDTO(u.getId(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
    }

    public MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderFromId(message.getSenderFromId());
        dto.setSenderToId(message.getSenderToId());
        dto.setContent(message.getContent());
        dto.setAttachments(message.getAttachments());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.isRead());

        regUser.findById(message.getSenderFromId())
                .ifPresent(user -> dto.setSenderFromName(user.getFirstName() + " " + user.getLastName()));
        regUser.findById(message.getSenderToId())
                .ifPresent(user -> dto.setSenderToName(user.getFirstName() + " " + user.getLastName()));

        return dto;
    }


}
