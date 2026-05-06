package com.danny.chatroom.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {
    private Long senderId;
    private String content;
}