package com.c.community.controller;

import com.c.community.entity.User;
import com.c.community.service.UserService;
import com.c.community.util.CommunityConstant;
import com.c.community.util.CommunityUtil;
import com.c.community.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.c.community.util.CommunityConstant.DEFAULT_EXPIRED_SECONDS;
import static com.c.community.util.CommunityConstant.IS_REMEMBERED_EXPIRED_SECONDS;

@Controller
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String getRegisterUser(User user, Model model) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "???????????????????????????????????????");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/register";
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    // http://localthost:8080/community/activation/${userId}/${code}
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int activationResult = userService.activation(userId, code);

        switch (activationResult) {
            case CommunityConstant.ACTIVATION_SUCCESS: {
                model.addAttribute("msg", "???????????????????????????????????????");
                model.addAttribute("target", "/login");
                break;
            }
            case CommunityConstant.ACTIVATION_REPEAT: {
                model.addAttribute("msg", "????????????");
                model.addAttribute("target", "/index");
                break;
            }
            default: {
                model.addAttribute("msg", "????????????");
                model.addAttribute("target", "/index");
            }
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpSession session, HttpServletResponse response) {
        // ???????????????
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // ??????????????????session
//        session.setAttribute("kaptcha", text);

        // ???????????????????????????
        String kaptcherOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptcherOwner", kaptcherOwner);
        cookie.setMaxAge(60);// 60????????????
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // ??????????????????redis
        String kaptchaKey = RedisKeyUtil.getKaptcha(kaptcherOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        // ???????????????????????????
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            LOGGER.error("?????????????????????" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code,
                        boolean isRemembered, Model model,
                        HttpSession session, HttpServletResponse response,
                        @CookieValue("kaptcherOwner") String kaptcherOwner) {
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptcherOwner)) {
            String redisKey = RedisKeyUtil.getKaptcha(kaptcherOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        // ???????????????
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "???????????????");
            return "/site/login";
        }

        // ?????????????????????
        int expiredSeconds = isRemembered ? IS_REMEMBERED_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
//        System.out.println(map);
        // service?????????map?????????ticket????????????
        if (map.containsKey("ticket")) {
            //?????????ticket?????????????????????????????????
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
