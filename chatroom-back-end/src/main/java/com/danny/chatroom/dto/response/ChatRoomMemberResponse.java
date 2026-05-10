package com.danny.chatroom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomMemberResponse {
    private Long chatRoomId;
    private Long userId;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;
}