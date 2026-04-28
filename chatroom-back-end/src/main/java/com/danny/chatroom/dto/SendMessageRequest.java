package com.danny.chatroom.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {
    private Long senderId;
    private String content;
}