package com.danny.chatroom.controller;

import com.danny.chatroom.dto.ChatMessageResponse;
import com.danny.chatroom.dto.ChatRoomResponse;
import com.danny.chatroom.dto.SendMessageRequest;
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

    // GET /api/chat/rooms?userId=1
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(@RequestParam Long userId) {
        List<ChatRoomResponse> rooms = chatService.getMyRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    // GET /api/chat/rooms/1/messages?userId=1
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @RequestParam Long userId) {
        List<ChatMessageResponse> messages = chatService.getMessages(roomId, userId);
        return ResponseEntity.ok(messages);
    }

    // POST /api/chat/rooms/1/messages
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long roomId,
            @RequestBody SendMessageRequest request) {
        ChatMessageResponse response = chatService.sendMessage(roomId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}