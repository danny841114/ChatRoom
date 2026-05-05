package com.danny.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class AddChatRoomRequest {
    private String name;
    private String type; // PRIVATE / GROUP
    private Set<Long> memberIds;
}
