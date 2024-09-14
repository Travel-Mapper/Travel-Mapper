package com.study.login;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class QrLoginController {

    private final String WEB_NAME = "localhost:8080";

    private final ConcurrentHashMap<String, SseEmitter> allEmitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> secreteRepo = new ConcurrentHashMap<>();


    @RequestMapping("/")
    public String testHome(){
        System.out.println("QrLoginController.testHome");
        return "QRpage";
    }

    @RequestMapping("/qr-login")
    public ResponseEntity makeQr(@RequestParam("mail") String mail) throws WriterException {
        // 사용자가 이메일 주소를 입력하면 qr 코드 이미지를 전송함

        // 유저 식별값 uuid
        // 이걸 sse 가 유지되는 동안 redis나 map 같은 곳에 저장 필요
        // 임시적으로 map 에 저장
        UUID uuid = UUID.randomUUID();
        String secret = uuid.toString().replaceAll("-", "");
        secreteRepo.put(mail, secret);

        int width = 200;
        int height = 200;
        String url = "http://" + WEB_NAME + "/sse-login-test?&mail=" + mail+"&secret="+secret;

        // QR 코드 정보 생성
        BitMatrix encode = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);

        // 정보를 바탕으로 QR 이미지를 만들어서 Stream 으로 브라우져로 보냄
        try {
            //output Stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            //Bitmatrix, file.format, outputStream
            MatrixToImageWriter.writeToStream(encode, "PNG", out);

            String qrUrl = Base64.getEncoder().encodeToString(out.toByteArray());

//            return qrUrl;
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrUrl);

        } catch (Exception e) {
            log.warn("QR Code OutputStream 도중 Excpetion 발생, {}", e.getMessage());
        }

        return null;
    }

    @RequestMapping("/sse-connect-request")
    public SseEmitter sseConnect(@RequestParam("mail") String mail){
        // sse 열기
        final SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(5)); // 5분
        allEmitters.put(mail, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("Connected")
                    .data("Connected successfully"));
            System.out.println("연결완료");
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }


    // QR 코드를 통한 사용자 식별값 고민중
    // id 값이나 session 값으로 하면 보안적으로 문제가 생김
    // uuid를 새롭게 만들어서
    @RequestMapping("/sse-login-test")
    public String connect(@RequestParam("secret") String secret, @RequestParam("mail") String mail) {
        System.out.println("QrLoginController.connect");
        SseEmitter emitter = allEmitters.get(mail);
        String savedSecret = secreteRepo.get(mail);

        if (!savedSecret.equals(secret)) {
            return "발급된 qr 링크와 접근 링크가 다름.";
        }

        // 클라이언트로 즉시 초기 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("Authorized")
                    .data("jwt 토큰값 같은거"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        // 연결이 종료되거나 타임아웃되면 자동으로 제거
        emitter.onCompletion(() -> System.out.println("SSE connection 종료: " + mail));
        emitter.onTimeout(() -> System.out.println("SSE connection 타임아웃: " + mail));

        return "정상 호출";
    }
}
