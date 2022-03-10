package com.c.community.dao;

import com.c.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper //MyBatis的注解
//@Repository spring的注解
public interface UserMapper {

    User selectById(int id);
    User selectByUsername(String uername);
    User selectByEmail(String email);

    int insertOneUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headUrl);

    int updatePassword(int id, String password);
}
