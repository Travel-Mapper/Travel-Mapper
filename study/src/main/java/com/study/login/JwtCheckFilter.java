package com.study.login;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class JwtCheckFilter extends HttpFilter {

    private final LoginService loginService;

    public JwtCheckFilter(LoginService myService) {
        this.loginService = myService;
    }
    
    /**
     * 접근 시도하는 유저에 대한 jwt 검증 필터
     * 검증 url 설정은
     */
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        // HttpServletRequest 객체에서 쿠키 배열 가져오기
        Cookie[] cookies = request.getCookies();

        // 이름이 token 인 쿠키 찾기
        Optional<Cookie> tokenCookie = Optional.ofNullable(cookies)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(cookie -> "token".equals(cookie.getName()))
                .findFirst();

        // 토큰 쿠키가 존재하면 유효성 체크. 없으면 에러
        if (tokenCookie.isPresent()) {
            String tokenValue = tokenCookie.get().getValue();
            try {
                loginService.parseJwt(tokenValue);
                response.setStatus(HttpStatus.OK.value());
                chain.doFilter(request, response);
            } catch (JwtException e) {
                chain.doFilter(request, response);
                e.printStackTrace();
                throw new RuntimeException("잘못된 jwt 값", e);
            }
        } else {
          // todo jwt 가 없는 요청 처리
            System.out.println("jwt 가 없음");
            chain.doFilter(request,response);
        }
    }
}
