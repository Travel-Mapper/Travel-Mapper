package com.study.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class ChatRoomResDto {
    private String roomId;
    private String roomName;
    private LocalDateTime regDate;

    @JsonIgnore // WebSocketSession 직렬화 방지
    private Set<WebSocketSession> sessions;

    public ChatRoomResDto() {

    }

    // 세션 수가 0인지 확인
    public boolean isSessionEmpty() {
        return this.sessions.size() == 0;
    }

    @Builder
    public ChatRoomResDto(String roomId, String roomName, LocalDateTime regDate) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.regDate = regDate;
        this.sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public void setCreatedAt(LocalDateTime now) {
    }
}