package com.c.community.dao;

import com.c.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    @Insert((
            "insert into login_ticket (user_id, ticket, status, expired) " +
                    "values (#{userId}, #{ticket}, #{status}, #{expired})"
    ))

    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    @Select((
            "select id, user_id, ticket, status, expired " +
                    "from login_ticket where ticket=#{ticket}"
    ))
    LoginTicket selectByTicket(String ticket);

    @Select((
            "select id, user_id, ticket, status, expired " +
                    "from login_ticket where user_id=#{userId}"
    ))
    LoginTicket selectByUserId(int userId);

    @Update((
            "update login_ticket " +
                    "set status = #{status} " +
                    "where ticket=#{ticket}"
    ))
    int updateTicketStatus(String ticket, int status);


}
