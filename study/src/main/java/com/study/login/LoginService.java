package com.study.login;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class LoginService {

    private final String FROM = "9669579@naver.com"; // 보내는 사람의 이메일 주소

    @Value("password")
    private String PASSWORD; // 보내는 사람의 이메일 계정 비밀번호
    private final String HOST = "smtp.naver.com"; // 구글 메일 서버 호스트 이름
    private final String MAIL_PATH = "C:\\Users\\hojun\\Desktop\\Git\\Travel-Mapper\\study\\src\\main\\resources\\templates\\QRpage.html";

    private final String WEB_NAME = "localhost:8080";
    private final ConcurrentHashMap<String, SseEmitter> allEmitters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> secreteRepo = new ConcurrentHashMap<>();

    private final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    /**
     * jwt 생성
     */
    public String createJwt(String id) {
        return Jwts.builder()
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
                .signWith(KEY)
                .compact();
    }

    /**
     * jwt 파싱
     */
    public String parseJwt(String token) {
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
        return claims.getBody().get("id", String.class);
    }

    /**
     * jwt 쿠키 생성
     */
    public Cookie makeJwtCookie(String jwt) {
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);  // 클라이언트 측 자바스크립트에서 접근 불가
        cookie.setMaxAge(60 * 60);  // 쿠키 유효 기간 1시간
        cookie.setPath("/");  // 사이트 전반에 걸쳐 쿠키가 전송되도록 설정
        return cookie;
    }

    /**
     * 메일 보내기
     */
    public void sendMail(String receiver, String qrImage) {

        Session session = getAuthenticationSession();

        String html = null;
        try {
            html = findHtmlAndChangeImage(qrImage);
        } catch (IOException e) {
            throw new RuntimeException("메일 html 파일 찾기 오류", e);
        }

        try {
            Message msg = writeMailMessage(session, receiver, "QR코드 전송", html);
            Transport.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("메일 전송 오류", e);
        }
    }

    private String findHtmlAndChangeImage(String qrImage) throws IOException {
        String html = new String(Files.readAllBytes(Paths.get(MAIL_PATH)), "UTF-8");
        return html.replace("{QR_CODE}", "data:image/png;base64," + qrImage);
    }

    /**
     * SMTP 프로토콜 설정
     */
    public Properties setSmtpProps(String host) {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.starttls.enable", "true"); // TLS를 활성화합니다.
        return props;
    }

    /**
     * 메일 세션 생성
     */
    public Session getAuthenticationSession() {

        Properties props = setSmtpProps(HOST);
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
            }
        });
        return session;
    }

    /**
     * 메일 메시지 생성
     */
    public Message writeMailMessage(Session session, String receiver, String title, String content) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
        msg.setSubject(title);
        msg.setContent(content, "text/html; charset=utf-8");
        return msg;
    }

    /**
     * QR 이미지 생성
     */

    public String makeQrCode(String mail) throws WriterException, IOException {
        // 임시적으로 map 에 저장
        UUID uuid = UUID.randomUUID();
        String secret = uuid.toString().replaceAll("-", "");

        saveQrSecrete(mail, secret);

        int width = 200;
        int height = 200;
        String url = "http://" + WEB_NAME + "/api/sse-qr-test?&mail=" + mail + "&secret=" + secret;

        // QR 코드 정보 생성
        BitMatrix encode = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);

        // 정보를 바탕으로 QR 이미지를 만들어서 Stream 으로 브라우져로 보냄
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //Bitmatrix, file.format, outputStream
        MatrixToImageWriter.writeToStream(encode, "PNG", out);

        String qr = Base64.getEncoder().encodeToString(out.toByteArray());
        return qr;
    }

    public void saveQrSecrete(String key, String value){
        secreteRepo.put(key, value);
    }

    public boolean validateSecret(String key, String target){
        return secreteRepo.get(key).equals(target);
    }

    public void saveSseEmitter(String key,SseEmitter emitter){
        allEmitters.put(key, emitter);
    }

    public SseEmitter getSseEmitter(String key){
        return allEmitters.get(key);
    }
}
