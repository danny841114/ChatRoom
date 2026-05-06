package com.danny.chatroom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long messageId;
    private Long roomId;
    private String roomName;
    private Long senderId;
    private String senderName;
    private String senderAccount;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
}