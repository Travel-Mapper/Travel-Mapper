package com.study.chat.controller;

import com.study.chat.dto.ChatRoomResDto;
import com.study.chat.entity.ChatRoom;
import com.study.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatRoom")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // 채팅방 개설
    @PostMapping("/new")
    public ResponseEntity<String> createRoom(@RequestBody Map<String, String> name) {
        String roomName = name.get("roomName");
        log.warn("roomName : {}", roomName);
        ChatRoomResDto room = chatService.createChatRoom(roomName);
        log.info("roomId : {}", room.getRoomId());
        return ResponseEntity.ok(room.getRoomId());
    }

    // 전체 채팅방 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<ChatRoom>> findAllRoom(){
        log.info("chatList : {}", chatService.findAll());
        return ResponseEntity.ok(chatService.findAll());
    }

    // 방 정보 가져오기
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoom> chatRoomInfo(@PathVariable String roomId) {
        ChatRoom room = chatService.findById(roomId);
        return ResponseEntity.ok(room);
    }

    // 메세지 저장하기

    // 해당 방의 최근 메세지 불러오기

    // 채팅 내역 삭제

    // 채팅방 삭제


    // 특정 채팅방 참여
    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomId, @RequestBody Map<String, String> user) {
        String username = user.get("username");
        chatService.joinChatRoom(roomId, username);
        log.info("{} 사용자가 채팅방 {}에 참여했습니다.", username, roomId);
        return ResponseEntity.ok(username + " 사용자가 채팅방 " + roomId + "에 참여했습니다.");
    }

    // 프로필 사진

    // 사진, 동영상 전송  s3, local() 사용
    @PostMapping("/{roomId}/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        ChatController fileService = null;
        String fileUrl = String.valueOf(fileService.uploadFile(file)); // fileService는 파일 업로드를 처리하는 서비스
        log.info("파일 업로드 완료: {}", fileUrl);
        return ResponseEntity.ok("파일이 업로드되었습니다: " + fileUrl);
    }

    // 사용자 채팅방 참여 인원 / 참여 인원 수
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<List<String>> getParticipants(@PathVariable String roomId) {
        List<String> participants = chatService.getParticipants(roomId);
        log.info("채팅방 {}의 참여 인원: {}", roomId, participants);
        return ResponseEntity.ok(participants);
    }

    // 사용자 채팅방 나가기 / 내용 저장
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable String roomId, @RequestBody Map<String, String> user) {
        String username = user.get("username");
        chatService.leaveChatRoom(roomId, username);
        log.info("{} 사용자가 채팅방 {}에서 나갔습니다.", username, roomId);
        return ResponseEntity.ok(username + " 사용자가 채팅방 " + roomId + "에서 나갔습니다.");
    }

    // 채팅방 메세지 전송
    @PostMapping("/{roomId}/message")
    public ResponseEntity<String> sendMessage(@PathVariable String roomId, @RequestBody Map<String, String> message) {
        String username = message.get("username");
        String content = message.get("content");
        chatService.sendMessage(roomId, username, content);
        log.info("채팅방 {}에 {}가 메시지를 보냈습니다: {}", roomId, username, content);
        return ResponseEntity.ok("메시지가 전송되었습니다.");
    }
}
