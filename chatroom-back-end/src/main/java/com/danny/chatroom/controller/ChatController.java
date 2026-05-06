package com.danny.chatroom.controller;

import com.danny.chatroom.dto.request.AddChatRoomRequest;
import com.danny.chatroom.dto.response.ChatMessageResponse;
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
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(@RequestParam Long userId) {
        List<ChatRoomResponse> rooms = chatService.getMyRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(@PathVariable Long roomId,
                                                    @RequestParam Long userId) {
        ChatRoomResponse room = chatService.getRoom(roomId, userId);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @RequestParam Long userId) {
        List<ChatMessageResponse> messages = chatService.getMessages(roomId, userId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> addChatRoomAndUsers(@RequestParam Long userId, @RequestBody AddChatRoomRequest request) {
        ChatRoomResponse room = chatService.addChatRoomAndUsers(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping("/rooms/users")
    public ResponseEntity<List<UserResponse>> getUsersExceptMe(@RequestParam Long userId) {
        List<UserResponse> users = chatService.getUsersExceptMe(userId);
        return ResponseEntity.ok(users);
    }
}