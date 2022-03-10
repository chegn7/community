package com.c.community.Service;

import com.c.community.dao.UserMapper;
import com.c.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUser(int userId) {
        return userMapper.selectById(userId);
    }
}
