package com.c.community.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {

    public static String getValue(String key, HttpServletRequest request) {
        if (request == null || StringUtils.isBlank(key)) throw new IllegalArgumentException("参数为空！");
        for (Cookie cookie : request.getCookies()) {
            if (key.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}
