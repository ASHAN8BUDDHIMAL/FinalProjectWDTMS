package com.example.demo.Controller;

import com.example.demo.DTO.ConversationUserDTO;
import com.example.demo.DTO.MessageDTO;
import com.example.demo.Service.MessageService;
import com.example.demo.Service.UserService;
import com.example.demo.model.Message;
import com.example.demo.model.UserRegistration;
import com.example.demo.repository.RegUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private RegUser regUser;

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO, HttpSession session) {
        Long senderFromId = (Long) session.getAttribute("loggedInUserId");
        if (senderFromId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        messageDTO.setSenderFromId(senderFromId);
        Message message = convertToEntity(messageDTO);
        Message savedMessage = messageService.saveMessage(message);
        return ResponseEntity.ok(convertToDTO(savedMessage));
    }

    @GetMapping("/conversation/{user2}")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @PathVariable Long user2,
            HttpSession session) {

        Long user1 = (Long) session.getAttribute("loggedInUserId");
        if (user1 == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Message> messages = messageService.getConversation(user1, user2);
        return ResponseEntity.ok(messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<MessageDTO>> getUnreadMessages(@PathVariable Long userId) {
        List<Message> messages = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
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
    public Message convertToEntity(MessageDTO dto) {
        Message message = new Message();
        message.setSenderFromId(dto.getSenderFromId());
        message.setSenderToId(dto.getSenderToId());
        message.setContent(dto.getContent());
        message.setAttachments(dto.getAttachments());
        return message;
    }

    @GetMapping("/users/search")
    public List<UserRegistration> searchUsers(@RequestParam String name) {
        return userService.searchUsersByName(name);
    }

    @GetMapping("/conversation-users")
    public List<ConversationUserDTO> getConversationUsers(HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            throw new RuntimeException("User is not logged in");
        }
        return messageService.getConversationUsers(loggedInUserId);
    }



}
