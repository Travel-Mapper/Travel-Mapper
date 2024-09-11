package com.study.login;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {

    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @RequestMapping("/login")
    public ResponseEntity<String> loginAction(@RequestParam("id") String id, @RequestParam("pw") String pw, HttpServletResponse response) {

        String jwt = createJwt(id);

        // 헤더에 JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("typ", "JWT");
        headers.add("Authorization", "Bearer " + jwt);

        // 프론트에서 추가적인 작업을 안하기 위해 cookie 로 jwt 관리
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);  // 클라이언트 측 자바스크립트에서 접근 불가
        cookie.setMaxAge(60 * 60);  // 쿠키 유효 기간 1시간
        cookie.setPath("/");  // 사이트 전반에 걸쳐 쿠키가 전송되도록 설정

        // 특정 페이지로 리다이렉트 할 계획 (예: 홈 페이지)
//        headers.add("Location", "/home");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/login/check")
    @ResponseBody
    public String checkLogin(@CookieValue("token") Cookie jwt, @RequestParam("id") String idInput) {
        String tokenValue = jwt.getValue();
        String id = parseJwt(tokenValue);
        if (!id.equals(idInput)) {
            return "다름: " + id;
        }
        return "같음: " + id;
    }

    public String createJwt(String id) {
        return Jwts.builder()
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
                .signWith(key)
                .compact();
    }

    public String parseJwt(String token) {
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
        return claims.getBody().get("id", String.class);
    }
}