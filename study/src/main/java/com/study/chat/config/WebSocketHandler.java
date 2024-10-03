package com.study.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.chat.dto.ChatMsgDto;
import com.study.chat.dto.ChatRoomReqDto;
import com.study.chat.dto.ChatRoomResDto;
import com.study.chat.entity.ChatRoom;
import com.study.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Component
// WebSocketHandler를 상속받아서 WebSocketHandler를 구현
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    // 세션과 roomId를 매핑하는 Map
    private final Map<WebSocketSession, String> sessionRoomIdMap = new ConcurrentHashMap<>();

    @Override
    // 클라이언트가 서버로 연결을 시도할 때 호출되는 메서드
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // 클라이언트가 전송한 메시지
        log.warn("{}", payload);
        // JSON 문자열을 ChatMessageDto 객체로 변환
        ChatMsgDto chatMsg = objectMapper.readValue(payload, ChatMsgDto.class);
        ChatRoom chatRoom = chatService.findById(chatMsg.getRoomId());

        System.out.println("chatRoom getRegDate() : " + chatRoom.getCreatedAt());
        sessionRoomIdMap.put(session, chatMsg.getRoomId()); // 세션과 채팅방 ID를 매핑
        System.out.println("sessionRoomIdMap : "+ sessionRoomIdMap);
        chatRoom.handlerActions(session, chatMsg, chatService);

    }

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//        String payload = message.getPayload();
//        log.warn("Received: " + payload);
//
//        // 메시지 파싱 및 오류 처리
//        try {
//            // objectMapper가 json데이터를 객체로 바꿔줌
//            ChatMsgDto chatMsg = objectMapper.readValue(payload, ChatMsgDto.class);
//
//            if (chatMsg == null || chatMsg.getRoomId() == null) {
//                log.error("Received a null ChatMsgDto or roomId.");
//                return;
//            }
//
//            String roomId = chatMsg.getRoomId();
//            sessionRoomIdMap.put(session, roomId); // 세션과 방 ID 매핑
//
//            // 메시지 유형에 따라 처리
//            if (chatMsg.getType() == ChatMsgDto.MessageType.ENTER) {
//                // 방에 들어갔을 때 처리
//                chatService.addSessionAndHandleEnter(roomId, session);
//            } else if (chatMsg.getType() == ChatMsgDto.MessageType.CLOSE) {
//                // 방을 나갔을 때 처리
//                chatService.removeSessionAndHandleExit(roomId, session);
//            } else if (chatMsg.getType() == ChatMsgDto.MessageType.CHAT) {
//                chatService.sendMsgToAll(roomId, chatMsg);
//            } else {
//                // 그 외 메시지 전달
//                chatService.sendMsgToAll(roomId, chatMsg);
//            }
//        } catch (IOException e) {
//            log.error("Error parsing chat message: {}", e.getMessage());
//            // 클라이언트에게 에러 메시지 전송 (선택 사항)
//            session.sendMessage(new TextMessage("Error: Invalid message format"));
//        }
//    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션과 매핑된 채팅방 ID 가져오기
        try {
            // 세션과 매핑된 채팅방 ID 가져오기
            log.warn("afterConnectionClosed: {}", session);
            String roomId = sessionRoomIdMap.remove(session);
            ChatRoom chatRoom = chatService.findById(roomId);
            if (chatRoom != null) {
                chatRoom.handleSessionClosed(session, chatService);
            } else {
                log.warn("Chat room not found for ID: {}", roomId);
            }
        } catch (Exception e) {
            log.error("Error in afterConnectionClosed", e);
        }
    }

//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        // 세션과 매핑된 채팅방 ID 가져오기
//        String roomId = sessionRoomIdMap.remove(session);
//
//        if (roomId != null) {
//            log.info("Session closed for roomId: {}", roomId);
//            // 방을 나갔을 때 처리
//            ChatMsgDto chatMsg = new ChatMsgDto();
//            chatMsg.setType(ChatMsgDto.MessageType.CLOSE);
//            chatService.removeSessionAndHandleExit(roomId, session);
//        } else {
//            log.warn("No roomId found for session closure");
//        }
//    }

//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        super.afterConnectionEstablished(session);
//        log.info("New WebSocket connection established: {}", session.getId());
//    }
//
//    // 세션이 닫히기 전에 종료 처리 등을 추가적으로 할 수 있음
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//        log.error("Error occurred for session {}: {}", session.getId(), exception.getMessage());
//        super.handleTransportError(session, exception);
//    }
}
