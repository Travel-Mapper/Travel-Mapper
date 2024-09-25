package com.study.chat.controller;

import com.study.chat.dto.ChatRoomResDto;
import com.study.chat.entity.ChatRoom;
import com.study.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // 채팅방 개설
    @PostMapping("/createroom")
    public ResponseEntity<String> createRoom(@RequestBody Map<String, String> name) {
        String roomName = name.get("roomName");
        log.warn("roomName : {}", roomName);
        ChatRoomResDto room = chatService.createChatRoom(roomName);
        log.info("roomId : {}", room.getRoomId());
        return ResponseEntity.ok(room.getRoomId());
    }

    // 방 리스트 반환
    @GetMapping("/list")
    public ResponseEntity<List<ChatRoom>> findAllRoom(){
        log.info("chatList : {}", chatService.findAll());
        return ResponseEntity.ok(chatService.findAll());
    }

    // 특정 방 조회
    @GetMapping("/chatroom/{roomId}")
    public ResponseEntity<ChatRoom> chatRoomInfo(@PathVariable String roomId) {
        ChatRoom room = chatService.findById(roomId);
        return ResponseEntity.ok(room);
    }

//        // 사용자 채팅방 참여
//    // /api/chatrooms/join 경로로 POST 요청 시 사용자가 특정 채팅방에 참여하고 해당 채팅방으로 리다이렉트함
//    @PostMapping("/chatrooms/join")
//    public String joinChatroom(@RequestParam String roomNum, @RequestParam String username, Model model) {
//        chatRoomService.addParticipant(roomNum, username);
//        return "redirect:/api/chatrooms/" + roomNum; // 채팅방 참여 후 해당 채팅방으로 리다이렉트
//    }
//
//    // 사용자 채팅방 나가기
//    // /api/chatrooms/leave 경로로 POST 요청 시 사용자가 특정 채팅방에서 나가고 채팅방 목록으로 리다이렉트함
//    @PostMapping("/chatrooms/leave")
//    public String leaveChatroom(@RequestParam String roomNum, @RequestParam String username) {
//        chatRoomService.removeParticipant(roomNum, username);
//        return "redirect:/api/chatroom"; // 채팅방 나가기 후 채팅방 목록으로 리다이렉트
//    }



//    // 채팅방 목록 조회
//    // /api/chatroom 경로로 GET 요청 시 모든 채팅방 목록을 조회하고, 이를 chatList.html 뷰에 전달함
//    @GetMapping("/chatroom")
//    public String chatList(Model model) {
//        List<ChatRoom> chatRooms = chatRoomService.findAllRooms();
//        model.addAttribute("chatRooms", chatRooms);
//        return "chatList"; // chatList.html로 렌더링
//    }
//
//    // 채팅방 메세지 전송
//    // /api/chatrooms/{chatroomId}/message 경로로 POST 요청 시 메시지와 송신자 정보를 받아 저장한 후 해당 채팅방으로 리다이렉트함
//    @PostMapping("/chatrooms/{chatroomId}/message")
//    public String sendMessage(@PathVariable String chatroomId, @RequestParam String message, @RequestParam String sender, Model model) {
//        ChatMsgDto chatDTO = new ChatMsgDto();
//        chatDTO.setRoomNum(Integer.parseInt(chatroomId));
//        chatDTO.setSender(sender);
//        chatDTO.setMsg(message);
//        chatService.save(new Chat(chatDTO)); // 메시지 저장
//        return "redirect:/api/chatrooms/" + chatroomId; // 메시지 전송 후 해당 채팅방으로 리다이렉트
//    }
//
//    // 채팅방 메세지 조회
//    // /api/chatrooms/{chatroomId} 경로로 GET 요청 시 주어진 채팅방 ID에 해당하는 모든 메시지를 조회하여 chatroom.html 뷰에 전달함
//    @GetMapping("/chatrooms/{chatroomId}")
//    public String getMessages(@PathVariable String chatroomId, Model model) {
//        List<Chat> messages = chatService.findMessagesByRoomNum(Integer.parseInt(chatroomId));
//        model.addAttribute("messages", messages);
//        return "chatroom"; // chatroom.html로 렌더링
//    }
}
