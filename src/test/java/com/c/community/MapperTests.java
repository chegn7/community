package com.c.community;

import com.c.community.dao.DiscussPostMapper;
import com.c.community.dao.LoginTicketMapper;
import com.c.community.dao.UserMapper;
import com.c.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser() {
        System.out.println(userMapper.selectById(11));
        System.out.println(userMapper.selectByUsername("liubei"));
        System.out.println(userMapper.selectByEmail("nowcoder1@sina.com"));
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("zhangsan");
        user.setPassword("zhangsan1234");
        int i = userMapper.insertOneUser(user);
        System.out.println(i == 1 ? "成功添加用户" : "添加用户失败");
    }

    @Test
    public void testUpdateUser() {
        int id = userMapper.selectByUsername("zhangsan").getId();
        System.out.println("原密码：" + userMapper.selectById(id).getPassword());
        userMapper.updatePassword(id, "newpassword");
        System.out.println("现密码：" + userMapper.selectById(id).getPassword());
    }

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectPost() {
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
//        System.out.println(discussPostMapper.selectDiscussPosts(0, 0, 10));
    }

    @Autowired
    LoginTicketMapper loginTicketMapper;


}
