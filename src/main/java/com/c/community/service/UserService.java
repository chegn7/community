package com.c.community.service;

import com.c.community.dao.LoginTicketMapper;
import com.c.community.dao.UserMapper;
import com.c.community.entity.LoginTicket;
import com.c.community.entity.User;
import com.c.community.util.CommunityConstant;
import com.c.community.util.CommunityUtil;
import com.c.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUser(int userId) {
        if (userId <= 0) return null;
        return userMapper.selectById(userId);
    }

    public User findUser(String username) {
        if (StringUtils.isBlank(username)) return null;
        return userMapper.selectByUsername(username);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //异常处理
        if (user == null) throw new IllegalArgumentException("用户不能为空！");

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        User user1;
        user1 = userMapper.selectByUsername(user.getUsername());
        if (user1 != null) {
            map.put("usernameMsg", "用户名已存在");
            return map;
        }
        user1 = userMapper.selectByEmail(user.getEmail());
        if (user1 != null) {
            map.put("emailMsg", "邮箱已注册");
            return map;
        }
        //对密码加密加盐
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID().substring(0, 6));
        String headUrl = String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1001));
        user.setHeaderUrl(headUrl);
        user.setCreateTime(new Date());
        userMapper.insertOneUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localthost:8080/community/activation/${userId}/${code}
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        System.out.println(url);
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活邮件", content);
        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            if (user.getStatus() == 1) return CommunityConstant.ACTIVATION_REPEAT;
            else if (user.getActivationCode().equals(code)) {
                userMapper.updateStatus(userId, 1);
                return CommunityConstant.ACTIVATION_SUCCESS;
            }
        }
        return CommunityConstant.ACTIVATION_FAILURE;
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            map.put("usernameMsg", "账号不存在");
            return map;
        }
        //验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活");
            return map;
        }
        System.out.println("password:" + password);
//        password = CommunityUtil.md5(password + user.getSalt());
        if (!validatePassword(password, user)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateTicketStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        if (StringUtils.isBlank(ticket)) return null;
        return loginTicketMapper.selectByTicket(ticket);
    }

    public LoginTicket findLoginTicket(int userId) {
        if (userMapper.selectById(userId) == null) return null;
        return loginTicketMapper.selectByUserId(userId);
    }

    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    public boolean validatePassword(String password, User user) {
        if (!StringUtils.isBlank(password)) {
            password = CommunityUtil.md5(password + user.getSalt());
            if (user.getPassword().equals(password)) return true;
        }
        return false;
    }

    public int updatePassword(int userId, String password) {
        return userMapper.updatePassword(userId, password);
    }


}
