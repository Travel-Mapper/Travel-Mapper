package com.study.login;

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
     * jwt 토큰 검증 및 jwt 토큰의 정보와 접근하려는 사용자 정보가 같은지 확인
     */
    @RequestMapping("/login/token")
    @ResponseBody
    public ResponseEntity checkJwt(@CookieValue("token") Cookie jwt, @RequestParam("id") String idInput) {
        String tokenValue = jwt.getValue();
        String id = loginService.parseJwt(tokenValue);
        if (!id.equals(idInput)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * qr 코드 로그인. sse 통신을 열면서 메일 주소로 qr이미지 메일 전송
     */
    @RequestMapping("/qr-login")
    public ResponseEntity makeQr(@RequestParam("mail") String mail) {

        try {
            String qrUrl = loginService.makeQrCode(mail);
            loginService.sendMail(mail);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrUrl);

        } catch (Exception e) {
            log.warn("QR Code OutputStream 도중 Excpetion 발생, {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @RequestMapping("/qr_mail")
    public SseEmitter qrMailSendAndSseConnection(@RequestParam("mail") String mail) {
        final SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(5)); // 5분
        loginService.saveSseEmitter(mail, emitter);

        String qrImage = null;

        try {
            qrImage = loginService.makeQrCode(mail);
            loginService.sendMail(mail, qrImage);

        } catch (Exception e) {
            log.warn("QR Code 생성 도중 Excpetion 발생, {}", e.getMessage());
        }

//        try {
//            emitter.send(SseEmitter.event()
//                    .name("QR CODE")
//                    .data(qrImage));
//        } catch (IOException e) {
//            emitter.completeWithError(e);
//        }
        return emitter;
    }
}