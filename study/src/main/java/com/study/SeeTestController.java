package com.study;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Controller
public class SeeTestController {

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @RequestMapping("/sse/{id}")
    public SseEmitter test(@PathVariable("id") String id) {
        System.out.println("SeeTestController.test");
        final SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(1)); // 1분
        emitters.put(id, emitter);

        // 클라이언트로 즉시 초기 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("message")
                    .data("Connected successfully for session: " + id));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        // 연결이 종료되거나 타임아웃되면 자동으로 제거
        emitter.onCompletion(() -> System.out.println("SSE connection 종료: " + id));
        emitter.onTimeout(() -> System.out.println("SSE connection 타임아웃: " + id));

        return emitter;
    }
    @RequestMapping("/sse2/{id}")
    public SseEmitter test2(@PathVariable("id") String id) throws IOException {
        final SseEmitter emitter = emitters.get(id);
        emitter.send(SseEmitter.event()
                        .id("idtest")
                .name("testEvent")
                .data("dataTest"));
        return emitter ;
    }
}
