package com.danny.chatroom.repository;

import com.danny.chatroom.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    boolean existsByChatRoomIdAndUserId(Long roomId, Long userId);
    List<ChatRoomMember> findByUserId(Long userId);
}