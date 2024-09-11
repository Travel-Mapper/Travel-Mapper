package com.study;

import org.apache.logging.log4j.util.Base64Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;

class ApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test1() throws Exception {

        String header = "{  \"alg\": \"HS256\",\n" + "  \"typ\": \"JWT\"}";
        String data = "{\"sub\": \"1234567890\",\n" + "  \"name\": \"John Doe\",\n" + "  \"iat\": 1516239022\n}";
        String secret = "yourSecretKeyHereShouldBeAtLeast32BytesLong";
        String signature = Base64Util.encode(header) + Base64Util.encode(data) + Base64Util.encode(secret);
        String jwt = header.concat("." + data).concat("." + signature);
        String encoded = hmacSha256(jwt, secret);

        System.out.println(encoded);
        Cookie cookie = new Cookie("jwt", encoded);

//			return encoded;


    }

    private static String hmacSha256(String data, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return new String(sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

}
