package com.study.login;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class LoginController {


    private final LoginService loginService;

    /**
     * 로그인 시도
     * 로그인 성공시 jwt 생성하여 쿠키로 발급
     * */
    @RequestMapping("/login")
    public ResponseEntity<String> loginAction(@RequestParam("id") String id, @RequestParam("pw") String pw, HttpServletResponse response) {

        // todo 로그인 시도 정보로 사용자 정보 조회

        String jwt = loginService.createJwt(id);
        // 헤더에 JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("typ", "JWT");
        headers.add("Authorization", "Bearer " + jwt);

        // 프론트에서 추가적인 작업을 안하기 위해 cookie 로 jwt 관리
        Cookie cookie = loginService.makeJwtCookie(jwt);

        // 특정 페이지로 리다이렉트 할 계획 (예: 홈 페이지)
        headers.add("Location", "/home");
        response.addCookie(cookie);
        return ResponseEntity.status(302).headers(headers).build();
    }

    /**
     * jwt 토큰 검증 및 jwt 토큰의 정보와 파라미터 id값과 같은지 확인
     */
    @RequestMapping("/login/check")
    public ResponseEntity checkJwt(@CookieValue("token") Cookie jwt, @RequestParam("id") String idInput) {
        String tokenValue = jwt.getValue();
        try {
            String id = loginService.parseJwt(tokenValue);
            if (!id.equals(idInput)) {
                return ResponseEntity.status(401).build();
            }
        }catch (JwtException e){
            e.printStackTrace();
            throw new RuntimeException("잘못된 jwt 값", e);
        }

        System.out.println("성공");
        
        return ResponseEntity.ok().build();
    }

    /**
     * qr 이미지를 메일로 전송
     * */
    @RequestMapping("/qr-mail")
    public SseEmitter qrMailSendAndSseConnection(@RequestParam("mail") String mail) {
        final SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(5)); // 5분간 sse 유지
        loginService.saveSseEmitter(mail, emitter);

        String qrImage = null;

        try {
            qrImage = loginService.makeQrCode(mail);
            loginService.sendMail(mail, qrImage);

        } catch (Exception e) {
            log.warn("QR Code 생성 도중 Excpetion 발생, {}", e.getMessage());
        }

        return emitter;
    }

    /**
     * qr 을 통해서 들어오는 링크
     * secret 값이 옳바르면 jwt 값을 http 바디로 전송
     * */
    @RequestMapping("/sse-qr-test")
    public ResponseEntity test(@RequestParam("mail") String mail, @RequestParam("secret") String secret){
        SseEmitter sseEmitter = loginService.getSseEmitter(mail);
        boolean validated = loginService.validateSecret(mail, secret);

        try {
            if(!validated){
                sseEmitter.send(SseEmitter.event()
                        .name("qr")
                        .data("wrong secret"));
            }
            sseEmitter.send(SseEmitter.event()
                    .name("qr")
                    .data("success"));

            // todo. 아이디 mail로 찾기
            String jwt = loginService.createJwt("ㅁㅇㄹ");
            // todo 성공시 jwt 토큰 발행. 토큰은 프론트에서 쿠키로 설정.
            sseEmitter.complete();
            return ResponseEntity.ok().body(jwt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}