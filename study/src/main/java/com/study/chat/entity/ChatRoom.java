package com.study.chat.entity;

import com.study.chat.dto.ChatMsgDto;
import com.study.chat.service.ChatService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;

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

    public void handlerActions(WebSocketSession session, ChatMsgDto chatMsg, ChatService chatService) {
    }

    public void handleSessionClosed(WebSocketSession session, ChatService chatService) {
    }
}
