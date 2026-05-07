package com.danny.chatroom.repository;

import com.danny.chatroom.entity.ChatRoom;
import com.danny.chatroom.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    boolean existsByChatRoomIdAndUserId(Long roomId, Long userId);

    ChatRoomMember findByChatRoomIdAndUserId(Long roomId, Long userId);

    List<ChatRoomMember> findByUserIdOrderByChatRoomLastMessageTimeDesc(Long userId);

    List<ChatRoomMember> findByChatRoomId(Long roomId);

    @Query("""
                SELECT COUNT(m.id)
                FROM ChatRoomMember rm
                JOIN rm.chatRoom.messages m
                WHERE rm.user.id = :userId
                  AND rm.chatRoom.id = :roomId
                  AND m.createdAt > rm.lastReadAt
                  AND m.sender.id <> :userId
                  AND m.deletedAt IS NULL
            """)
    Long countUnreadMessages(@Param("userId") Long userId, @Param("roomId") Long roomId);

    @Query("""
                SELECT DISTINCT crm.chatRoom
                FROM ChatRoomMember crm
                WHERE crm.user.id = :userId
                  AND crm.chatRoom.type = 'PRIVATE'
            """)
    List<ChatRoom> findPrivateRoomsByUserId(Long userId);
}