package com.danny.chatroom.service;

import com.danny.chatroom.dto.request.AddChatRoomRequest;
import com.danny.chatroom.dto.request.SendMessageRequest;
import com.danny.chatroom.dto.response.ChatMessageResponse;
import com.danny.chatroom.dto.response.ChatRoomResponse;
import com.danny.chatroom.dto.response.UserResponse;
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
                .map(chatRoomMember -> {
                    ChatRoom room = chatRoomMember.getChatRoom();

                    Long count = chatRoomMemberRepository.countUnreadMessages(userId, room.getId());

                    if ("PRIVATE".equals(room.getType())) {
                        ChatRoomMember c = room.getMembers().stream()
                                .filter(m -> !Objects.equals(m.getUser().getId(), userId))
                                .toList()
                                .get(0);
                        room.setName(c.getUser().getUsername());
                    }

                    return new ChatRoomResponse(
                            room.getId(),
                            room.getName(),
                            room.getType(),
                            count,
                            null
                    );
                })
                .toList();
    }

    public ChatRoomResponse getRoom(Long roomId, Long userId) {
        checkRoomMember(roomId, userId);

        Long count = chatRoomMemberRepository.countUnreadMessages(userId, roomId);

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

            if ("PRIVATE".equals(chatRoom.getType())) {
                ChatRoomMember chatRoomMember = chatRoom.getMembers().stream()
                        .filter(m -> !Objects.equals(m.getUser().getId(), userId))
                        .toList()
                        .get(0);
                chatRoom.setName(chatRoomMember.getUser().getUsername());
            }

            return new ChatRoomResponse(
                    chatRoom.getId(),
                    chatRoom.getName(),
                    chatRoom.getType(),
                    count,
                    users
            );
        }
        return null;
    }

    public List<ChatMessageResponse> getMessages(Long roomId, Long userId) {
        checkRoomMember(roomId, userId);

        // update last read time
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByChatRoomIdAndUserId(roomId, userId);
        chatRoomMember.setLastReadAt(LocalDateTime.now());
        chatRoomMemberRepository.save(chatRoomMember);

        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    public void deleteMessage(Long roomId, Long messageId, Long userId) {
        checkRoomMember(roomId, userId);

        Optional<ChatMessage> messageOptional = chatMessageRepository.findById(messageId);
        if (messageOptional.isEmpty()) throw new EntityNotFoundException("不存在此訊息");

        ChatMessage chatMessage = messageOptional.get();
        chatMessage.setDeletedAt(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
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

    public ChatRoomResponse addChatRoom(Long userId,
                                        AddChatRoomRequest addChatRoomRequest) {
        Set<Long> memberIds = addChatRoomRequest.getMemberIds();
        String type = addChatRoomRequest.getType();

        if ("PRIVATE".equals(type)) {
            Long targetUserId = memberIds.iterator().next(); // get next factor

            List<ChatRoom> privateRooms = chatRoomMemberRepository.findPrivateRoomsByUserId(userId);

            for (ChatRoom room : privateRooms) {
                List<ChatRoomMember> members = room.getMembers();   // two ChatRoomMember object

                boolean existed = room.getMembers().stream()
                        .anyMatch(m -> m.getUser().getId().equals(targetUserId));

                if (existed) {
                    return new ChatRoomResponse(
                            room.getId(),
                            room.getName(),
                            room.getType(),
                            0L,
                            null
                    );
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();

        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setName(addChatRoomRequest.getName());
        newChatRoom.setType(type);
        newChatRoom.setCreatedAt(now);
        newChatRoom.setLastMessageTime(now);
        chatRoomRepository.save(newChatRoom);

        List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

        memberIds.add(userId);
        for (Long memberId : memberIds) {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("使用者不存在"));

            ChatRoomMember chatRoomMember = new ChatRoomMember();
            chatRoomMember.setUser(user);
            chatRoomMember.setChatRoom(newChatRoom);
            chatRoomMember.setJoinedAt(now);
            chatRoomMember.setLastReadAt(now);

            chatRoomMembers.add(chatRoomMember);
        }
        chatRoomMemberRepository.saveAll(chatRoomMembers);

        return new ChatRoomResponse(
                newChatRoom.getId(),
                newChatRoom.getName(),
                newChatRoom.getType(),
                0L,
                null
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
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getSender().getAccount(),
                message.getContent(),
                message.getMessageType(),
                message.getCreatedAt(),
                message.getDeletedAt()
        );
    }
}