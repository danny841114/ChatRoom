package com.danny.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomResponse {
    private Long roomId;
    private String name;
    private String type;
}