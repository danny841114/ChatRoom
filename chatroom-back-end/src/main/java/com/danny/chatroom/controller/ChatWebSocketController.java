package com.danny.chatroom.controller;

import com.danny.chatroom.dto.ChatMessageResponse;
import com.danny.chatroom.dto.ChatRoomResponse;
import com.danny.chatroom.dto.SendMessageRequest;
import com.danny.chatroom.entity.ChatRoomMember;
import com.danny.chatroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatWebSocketController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, SendMessageRequest request) {
        ChatMessageResponse savedMessage = chatService.sendMessage(roomId, request);
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, savedMessage);

        List<ChatRoomMember> chatRoomMembers = chatService.findByChatRoomId(roomId);
        for (ChatRoomMember member : chatRoomMembers) {
            Long memberId = member.getUser().getId();
            List<ChatRoomResponse> myRooms = chatService.getMyRooms(memberId);
            messagingTemplate.convertAndSend("/topic/users/" + memberId + "/rooms", myRooms);
        }
    }
}
