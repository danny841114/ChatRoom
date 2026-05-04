package com.danny.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomResponse {
    private Long roomId;
    private String name;
    private String type;
    private List<User> users;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class User{
        private Long id;
        private String username;
        private String account;
    }
}