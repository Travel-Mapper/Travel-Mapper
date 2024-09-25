package com.study.chat.mapper;

import com.study.chat.dto.ChatRoomResDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatRoomResMappaer {
    @Insert("INSERT INTO chat_room (room_id, room_name, created_at) VALUES (#{roomId}, #{roomName}, #{createdAt})")
    void createRoom(ChatRoomResDto chatRoom);

    @Select("SELECT * FROM chat_room WHERE room_id = #{roomId}")
    ChatRoomResDto findRoomById(String roomId);

    @Select("SELECT * FROM chat_room")
    List<ChatRoomResDto> findAllRooms();

    @Update("UPDATE chat_room SET room_name = #{roomName} WHERE room_id = #{roomId}")
    void updateRoom(ChatRoomResDto chatRoom);

    @Delete("DELETE FROM chat_room WHERE room_id = #{roomId}")
    void deleteRoom(String roomId);
}
