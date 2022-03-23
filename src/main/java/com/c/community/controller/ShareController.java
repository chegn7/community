package com.c.community.controller;

import com.c.community.entity.Event;
import com.c.community.event.EventProducer;
import com.c.community.util.CommunityConstant;
import com.c.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    EventProducer eventProducer;

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Value("${wk.image.storage}")
    String wkImagePath;

    @RequestMapping(path = "/share", method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl) {
        String fileName = CommunityUtil.generateUUID();

        Event event = new Event()
                .setTopic(CommunityConstant.TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);
        // 返回访问路径
        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);

        return CommunityUtil.getJSONString(0, null, map);
    }

    @RequestMapping(path = "/share/image/{fileName}", method = RequestMethod.GET)
    public void getShareImage(@PathVariable("fileName") String fileName,
                              HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空！");
        }
        response.setContentType("image/png");
        File file = new File(wkImagePath + "/" + fileName + ".png");
        try (OutputStream outputStream = response.getOutputStream()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0 , b);
            }
        } catch (IOException e) {
            LOGGER.error("获取长图失败" + e.getMessage());
        }

    }


}
