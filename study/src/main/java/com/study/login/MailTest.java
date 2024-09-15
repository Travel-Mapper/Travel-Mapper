package com.study.login;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MailTest {

    public static void main(String[] args) throws MessagingException, IOException {
        MailTest mailTest = new MailTest();
        mailTest.test();
    }

    public void test() throws MessagingException, IOException {

        String to = "9669579@gmail.com"; // 받는 사람의 이메일 주소
        String from = "9669579@naver.com"; // 보내는 사람의 이메일 주소
        String password = ""; // 보내는 사람의 이메일 계정 비밀번호
        String host = "smtp.naver.com"; // 구글 메일 서버 호스트 이름

        // SMTP 프로토콜 설정
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.starttls.enable", "true"); // TLS를 활성화합니다.


        // 보내는 사람 계정 정보 설정
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        String html = new String(Files.readAllBytes(Paths.get("C:\\Users\\hojun\\Desktop\\Git\\Travel-Mapper\\study\\src\\main\\resources\\templates\\QRpage.html")), "UTF-8");

        // 메일 내용 작성
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject("ttt");
        msg.setContent(html,"text/html; charset=utf-8");

        // 메일 보내기
        Transport.send(msg);
    }
}
