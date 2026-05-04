package com.danny.chatroom.controller;

import com.danny.chatroom.dto.ChatMessageResponse;
import com.danny.chatroom.dto.SendMessageRequest;
import com.danny.chatroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatWebSocketController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, SendMessageRequest request) {
        ChatMessageResponse savedMessage = chatService.sendMessage(roomId, request);

        // 推送給訂閱 /topic/rooms/{roomId} 的所有前端
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, savedMessage);
    }
}
