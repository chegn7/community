package com.c.community.controller.interceptor;

import com.c.community.entity.User;
import com.c.community.service.MessageService;
import com.c.community.util.HostHolder;
import org.apache.ibatis.plugin.Intercepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int letterCount = messageService.findUnreadLettersCount(user.getId(), null);
            int noticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
            modelAndView.addObject("totalUnreadCount", letterCount + noticeCount);

        }
    }
}
