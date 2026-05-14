package com.danny.chatroom.controller;

import com.danny.chatroom.dto.request.AddChatRoomRequest;
import com.danny.chatroom.dto.request.AddMemberRequest;
import com.danny.chatroom.dto.response.ChatMessageResponse;
import com.danny.chatroom.dto.response.ChatRoomMemberResponse;
import com.danny.chatroom.dto.response.ChatRoomResponse;
import com.danny.chatroom.dto.response.UserResponse;
import com.danny.chatroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(@RequestParam Long userId) {
        List<ChatRoomResponse> rooms = chatService.getMyRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(@PathVariable Long roomId,
                                                    @RequestParam Long userId) {
        ChatRoomResponse room = chatService.getRoom(roomId, userId);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @RequestParam Long userId) {
        List<ChatMessageResponse> messages = chatService.getMessages(roomId, userId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{roomId}/messages")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long roomId,
            @RequestParam Long messageId,
            @RequestParam Long userId) {
        try {
            chatService.deleteMessage(roomId, messageId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestParam Long userId,
                                                           @RequestBody AddChatRoomRequest request) {
        ChatRoomResponse room = chatService.createChatRoom(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsersExceptMe(@RequestParam Long userId) {
        List<UserResponse> users = chatService.getUsersExceptMe(userId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/add")
    public ResponseEntity<List<UserResponse>> getUsersExceptExistingMembers(@RequestParam Long roomId,
                                                                            @RequestParam Long userId) {
        List<UserResponse> users = chatService.getUsersExceptExistingMembers(roomId, userId);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{roomId}/user/{deleteUserId}")
    public ResponseEntity<Void> deleteChatRoomUser(@PathVariable Long roomId,
                                                   @PathVariable Long deleteUserId,
                                                   @RequestParam Long userId) {
        chatService.deleteChatRoomUser(roomId, deleteUserId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/users")
    public ResponseEntity<List<ChatRoomMemberResponse>> addChatRoomUsers(@PathVariable Long roomId,
                                                                         @RequestBody AddMemberRequest request,
                                                                         @RequestParam Long userId) {
        List<ChatRoomMemberResponse> responses = chatService.addChatRoomUsers(roomId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}