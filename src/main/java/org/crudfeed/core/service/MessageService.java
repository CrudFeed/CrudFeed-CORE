package org.crudfeed.core.service;

import org.crudfeed.core.entity.Message;
import org.crudfeed.core.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;


    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public String sendMessage(long senderId, long receiverId, String content) {
        if (senderId <= 0 || receiverId <= 0 || senderId == receiverId) {
            return "Invalid sender or receiver!";
        }
        if (content == null || content.isBlank()) {
            return "Message cannot be empty!";
        }

        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .build();

        return messageRepository.writeMessage(message)
                ? "Message sent!"
                : "Could not create message!";
    }

    public List<Message> viewMessages(long senderId, long receiverId) {
        if (receiverId == -1) {
            return List.of();
        }

        return messageRepository.viewMessages(senderId, receiverId);
    }

    public String editMessage(long messageId, String text, long senderId) {
        if (text == null || text.isBlank()) {
            return "Message cannot be empty!";
        }

        if (text.length() > 2000) {
            return "Message is too long!";
        }

        return messageRepository.editMessage(messageId, text, senderId)
                ? "Message successfully updated!"
                : "Could not update message!";
    }

    public String deleteMessage(long messageId, long senderId) {
        if (messageId <= 0) {
            return "Invalid message id!";
        }

        return messageRepository.deleteMessage(messageId, senderId)
                ? "Message successfully deleted!"
                : "Could not delete message!";
    }
}