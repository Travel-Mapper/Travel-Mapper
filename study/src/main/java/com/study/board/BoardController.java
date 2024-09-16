package com.study.board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/boards")
public class BoardController {
    @GetMapping("/all")
    public String getBoardList() {
        return "/boards/boardList";
    }

    @GetMapping("/write")
    public String boardWrite(){
        return "/boards/boardWrite";
    }
}
