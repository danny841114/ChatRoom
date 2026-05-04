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

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getRooms(@RequestParam Long userId) {
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
}