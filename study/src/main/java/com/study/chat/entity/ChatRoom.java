package com.study.chat.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatRoom {
    private String roomId;       // 방 ID
    private String roomName;     // 방 이름
    private LocalDateTime createdAt; // 생성 시간

    public ChatRoom(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.createdAt = LocalDateTime.now();
    }
}
