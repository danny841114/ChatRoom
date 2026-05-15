package com.danny.chatroom.controller;

import com.danny.chatroom.dto.UserDetailsImpl;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ChatRoomResponse> rooms = chatService.getMyRooms(userDetails.getId());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(@PathVariable Long roomId,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ChatRoomResponse room = chatService.getRoom(roomId, userDetails.getId());
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long roomId,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ChatMessageResponse> messages = chatService.getMessages(roomId, userDetails.getId());
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{roomId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long roomId,
                                           @PathVariable Long messageId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            chatService.deleteMessage(roomId, messageId, userDetails.getId());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody AddChatRoomRequest request,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ChatRoomResponse room = chatService.createChatRoom(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping("/available-users")
    public ResponseEntity<List<UserResponse>> getUsersExceptMe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserResponse> users = chatService.getUsersExceptMe(userDetails.getId());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{roomId}/available-users")
    public ResponseEntity<List<UserResponse>> getUsersExceptExisting(@PathVariable Long roomId,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserResponse> users = chatService.getUsersExceptExistingMembers(roomId, userDetails.getId());
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{roomId}/users/{deleteUserId}")
    public ResponseEntity<Void> deleteChatRoomUser(@PathVariable Long roomId,
                                                   @PathVariable Long deleteUserId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatService.deleteChatRoomUser(roomId, deleteUserId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/users")
    public ResponseEntity<List<ChatRoomMemberResponse>> addChatRoomUsers(@PathVariable Long roomId,
                                                                         @RequestBody AddMemberRequest request,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ChatRoomMemberResponse> responses = chatService.addChatRoomUsers(roomId, request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}