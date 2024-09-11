package com.study;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class TestController {

    @RequestMapping("/get")
    public String makeJwt() throws Exception {

        String header = "{\"alg\": \"HS256\",\n" + "  \"typ\": \"JWT\"}";
        String data = "{\"sub\": \"1234567890\",\n" + "  \"name\": \"John Doe\",\n" + "  \"iat\": 1516239022\n}";
        String secret = "yourSecretKeyHereShouldBeAtLeast32BytesLong";

        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(data.getBytes(StandardCharsets.UTF_8));

        String BeforeSignature = encodedHeader + "." + encodedPayload;
        byte[] signature = hmacSha256(BeforeSignature, secret);
        String encodedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        String jwt = encodedHeader + "." + encodedPayload + "." + encodedSignature;

        System.out.println(jwt);

        return jwt;
    }

    private static byte[] hmacSha256(String data, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

}
