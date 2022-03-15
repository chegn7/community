package com.c.community.dao;

import com.c.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoginTicketMapperTest {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    void insertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(12);
        loginTicket.setStatus(0);
        loginTicket.setTicket("test1");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 20));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    void selectByTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("tesst");
        System.out.println(loginTicket);
    }

    @Test
    void updateTicketStatus() {
        loginTicketMapper.updateTicketStatus("test1", 1);
    }
}