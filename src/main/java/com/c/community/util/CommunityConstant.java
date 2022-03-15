package com.c.community.util;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认账号密码保存时间,默认记12个小时
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 勾选记住我账号密码保存时间，记7天
     */
    int IS_REMEMBERED_EXPIRED_SECONDS = DEFAULT_EXPIRED_SECONDS * 2 * 7;
}
