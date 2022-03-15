package com.c.community;

import com.c.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Test
    public void testTextMail() {
        mailClient.sendMail("1030889357@qq.com", "smtp发送邮件", "这是Java程序客户端发送的邮件");
    }

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testHTMLMail() {
        Context context = new Context();
        context.setVariable("username", "tom112");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("1030889357@qq.com", "html邮件1", content);
    }
}
