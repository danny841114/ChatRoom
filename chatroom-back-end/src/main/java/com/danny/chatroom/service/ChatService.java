package com.danny.chatroom.service;

import com.danny.chatroom.dto.*;
import com.danny.chatroom.entity.ChatMessage;
import com.danny.chatroom.entity.ChatRoom;
import com.danny.chatroom.entity.ChatRoomMember;
import com.danny.chatroom.entity.User;
import com.danny.chatroom.repository.ChatMessageRepository;
import com.danny.chatroom.repository.ChatRoomMemberRepository;
import com.danny.chatroom.repository.ChatRoomRepository;
import com.danny.chatroom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMyRooms(Long userId) {
        return chatRoomMemberRepository.findByUserIdOrderByChatRoomLastMessageTimeDesc(userId)
                .stream()
                .map(member -> {
                    ChatRoom room = member.getChatRoom();
                    List<ChatRoomResponse.User> users = room.getMembers()
                            .stream()
                            .map(m ->
                                    new ChatRoomResponse.User(
                                            m.getUser().getId(),
                                            m.getUser().getAccount(),
                                            m.getUser().getUsername()
                                    )
                            )
                            .toList();

                    return new ChatRoomResponse(
                            room.getId(),
                            room.getName(),
                            room.getType(),
                            users
                    );
                })
                .toList();
    }

    public ChatRoomResponse getRoom(Long roomId, Long userId) {
        checkRoomMember(roomId, userId);

        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(roomId);
        if (chatRoomOptional.isPresent()) {
            ChatRoom chatRoom = chatRoomOptional.get();

            List<ChatRoomResponse.User> users = chatRoom.getMembers()
                    .stream()
                    .map(m ->
                            new ChatRoomResponse.User(
                                    m.getUser().getId(),
                                    m.getUser().getAccount(),
                                    m.getUser().getUsername()
                            )
                    )
                    .toList();

            return new ChatRoomResponse(
                    chatRoom.getId(),
                    chatRoom.getName(),
                    chatRoom.getType(),
                    users
            );
        }
        return null;
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

        LocalDateTime now = LocalDateTime.now();

        room.setLastMessageTime(now);
        chatRoomRepository.save(room);

        ChatMessage message = new ChatMessage();
        message.setChatRoom(room);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setMessageType("TEXT");
        message.setCreatedAt(now);
        ChatMessage savedMessage = chatMessageRepository.save(message);

        return toMessageResponse(savedMessage);
    }

    public List<ChatRoomMember> findByChatRoomId(Long roomId) {
        return chatRoomMemberRepository.findByChatRoomId(roomId);
    }

    public ChatRoomResponse addChatRoomAndUsers(Long userId, AddChatRoomRequest addChatRoomRequest) {
        LocalDateTime now = LocalDateTime.now();

        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setName(addChatRoomRequest.getName());
        newChatRoom.setType(addChatRoomRequest.getType());
        newChatRoom.setCreatedAt(now);
        newChatRoom.setLastMessageTime(now);
        chatRoomRepository.save(newChatRoom);

        List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

        Set<Long> memberIds = addChatRoomRequest.getMemberIds();
        memberIds.add(userId);
        for (Long memberId : memberIds) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("使用者不存在"));

            ChatRoomMember chatRoomMember = new ChatRoomMember();
            chatRoomMember.setUser(user);
            chatRoomMember.setChatRoom(newChatRoom);
            chatRoomMember.setJoinedAt(now);

            chatRoomMembers.add(chatRoomMember);
        }
        chatRoomMemberRepository.saveAll(chatRoomMembers);

        List<ChatRoomResponse.User> users = chatRoomMembers
                .stream()
                .map(chatRoomMember -> {
                    User user = chatRoomMember.getUser();
                    return new ChatRoomResponse.User(
                            user.getId(),
                            user.getAccount(),
                            user.getUsername()
                    );
                })
                .toList();

        return new ChatRoomResponse(
                newChatRoom.getId(),
                newChatRoom.getName(),
                newChatRoom.getType(),
                users
        );
    }

    public List<UserResponse> getUsersExceptMe(Long userId) {
        List<User> allUsers = userRepository.findAll();

        return allUsers
                .stream()
                .filter(user -> !Objects.equals(user.getId(), userId))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getAccount(),
                        user.getUsername()))
                .toList();
    }

    private void checkRoomMember(Long roomId, Long userId) {
        boolean isMember = chatRoomMemberRepository.existsByChatRoomIdAndUserId(roomId, userId);

        if (!isMember) throw new EntityNotFoundException("不是聊天室成員，無法操作");
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChatRoom().getId(),
                message.getChatRoom().getName(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getSender().getAccount(),
                message.getContent(),
                message.getMessageType(),
                message.getCreatedAt()
        );
    }
}