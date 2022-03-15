package com.c.community.util;

import com.c.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，代替Session对象
 * 用threadlocal实现线程隔离
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    // 请求结束时清理user
    public void clear() {
        users.remove();
    }
}
