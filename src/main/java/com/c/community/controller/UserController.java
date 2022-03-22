package com.c.community.controller;

import com.c.community.annotation.LoginRequired;
import com.c.community.entity.LoginTicket;
import com.c.community.entity.User;
import com.c.community.service.FollowService;
import com.c.community.service.LikeService;
import com.c.community.service.UserService;
import com.c.community.util.CommunityConstant;
import com.c.community.util.CommunityUtil;
import com.c.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;


    // 用户设置页面
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "未选择图片");
            return "/site/setting";
        }

        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "图片格式不正确");
            return "/site/setting";
        }
        // 生成随机文件名
        String fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常", e);
        }

        // 更新外部访问路径
        User user = hostHolder.getUser();
        // http:xxxx.xxxx.xxxx/community/user/header/xxxxx.png
        String headUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 找到服务器图片存放位置
        fileName = uploadPath + "/" + fileName;
        // 输出格式
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                // Java 7 语法，在try括号里的会自动在finally里关闭
                FileInputStream fileInputStream = new FileInputStream(fileName);
                OutputStream outputStream = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("读取头像失败" + e.getMessage());
        }
    }

//    @LoginRequired
//    @RequestMapping(path = "/update_password", method = RequestMethod.POST)
//    public String updatePassword(String originalPassword, String newPassword, Model model) {
//        if (StringUtils.isBlank(originalPassword)) {
//            model.addAttribute("error", "原密码不能为空");
//            return getSettingPage();
//        }
//        if (StringUtils.isBlank(newPassword)) {
//            model.addAttribute("error", "新密码不能为空");
//            return getSettingPage();
//        }
//        // 验证原密码是否正确
//        User user = hostHolder.getUser();
//        int userId = user.getId();
//        if (userService.validatePassword(originalPassword, user)) {
//            newPassword = CommunityUtil.md5(newPassword + user.getSalt());
//            userService.updatePassword(userId, newPassword);
//
//            model.addAttribute("msg", "修改密码成功，转向登录界面");
//            model.addAttribute("target", "/login");
//            return "/site/operate-result";
//        } else {
//            model.addAttribute("error", "原密码错误");
//            return getSettingPage();
//        }
//    }

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUser(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        int userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", userLikeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(user.getId(), CommunityConstant.ENTITY_USER);
        //粉丝数量
        long followerCount = followService.findFollowerCount(CommunityConstant.ENTITY_USER, user.getId());
        //关注状态
        User loginUser = hostHolder.getUser();

        int followStatus = CommunityConstant.UNFOLLOW_STATUS;
        if (loginUser != null) {
            followStatus = followService.findFollowStatus(loginUser.getId(), CommunityConstant.ENTITY_USER, user.getId());
        }
        model.addAttribute("followStatus", followStatus);
        model.addAttribute("followeeCount", followeeCount);
        model.addAttribute("followerCount", followerCount);
        int loginUserId = user.getId();
        if (loginUser != null) loginUserId = loginUser.getId();
        model.addAttribute("loginUserId", loginUserId);

        return "/site/profile";
    }


}
