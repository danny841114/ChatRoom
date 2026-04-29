package com.danny.chatroom.dto;

import lombok.Getter;

@Getter
public class ChatWebSocketRequest {
    private Long senderId;
    private String content;
}
