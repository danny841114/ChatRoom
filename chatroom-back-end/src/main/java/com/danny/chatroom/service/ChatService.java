package com.danny.chatroom.service;

import com.danny.chatroom.dto.ChatMessageResponse;
import com.danny.chatroom.dto.ChatRoomResponse;
import com.danny.chatroom.dto.SendMessageRequest;
import com.danny.chatroom.entity.ChatMessage;
import com.danny.chatroom.entity.ChatRoom;
import com.danny.chatroom.entity.User;
import com.danny.chatroom.repository.ChatMessageRepository;
import com.danny.chatroom.repository.ChatRoomMemberRepository;
import com.danny.chatroom.repository.ChatRoomRepository;
import com.danny.chatroom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public List<ChatRoomResponse> getMyRooms(Long userId) {
        return chatRoomMemberRepository.findByUserId(userId)
                .stream()
                .map(member -> {
                    ChatRoom room = member.getChatRoom();
                    return new ChatRoomResponse(
                            room.getId(),
                            room.getName(),
                            room.getType()
                    );
                })
                .toList();
    }

    public List<ChatMessageResponse> getMessages(Long roomId, Long userId) {
        checkRoomMember(roomId, userId);

        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long roomId, SendMessageRequest request) {
        Long senderId = request.getSenderId();

        checkRoomMember(roomId, senderId);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("聊天室不存在"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("使用者不存在"));

        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setMessageType("TEXT");
        message.setCreatedAt(LocalDateTime.now());

        ChatMessage savedMessage = chatMessageRepository.save(message);

        return toMessageResponse(savedMessage);
    }

    private void checkRoomMember(Long roomId, Long userId) {
        boolean isMember = chatRoomMemberRepository.existsByChatRoomIdAndUserId(roomId, userId);

        if (!isMember) throw new AccessDeniedException("不是聊天室成員，無法操作");
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChatRoom().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getContent(),
                message.getMessageType(),
                message.getCreatedAt()
        );
    }
}