package com.study;

import com.study.chat.mapper.ChatRoomMapper;
import com.study.chat.service.ChatService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

//		Map<String, List<String>> roomData = new HashMap<>();
//		roomData.put("room1", Arrays.asList("User1", "User2", "User3"));
//		roomData.put("room2", Arrays.asList("User4", "User5"));
//
//		ChatService chatService = new ChatService((ChatRoomMapper) roomData);
//
//		List<String> participants = chatService.getParticipants("room1");
//		System.out.println(participants);  // 출력: [User1, User2, User3]
//
//		participants = chatService.getParticipants("room3");
//		System.out.println(participants);  // 출력: []
	}
}
