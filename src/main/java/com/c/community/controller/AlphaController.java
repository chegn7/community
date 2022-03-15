package com.c.community.controller;

import com.c.community.service.AlphaService;
import com.c.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    @ResponseBody
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + "\t" + value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try (
                PrintWriter writer = response.getWriter()
        ) {
            writer.write("<h1>testHTML<h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //GET请求处理

    // /students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student?id=123
    // /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    //也可以在参数前加@RequestParam注解
    public String addStudent(String name, String age) {
        System.out.println(name);
        System.out.println(age);
        return "save successfully";
    }

    // 响应动态HTML数据

    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "zhangsan");
        mav.addObject("age", 30);
        mav.setViewName("/demo/view");//view.html
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "school1");
        model.addAttribute("age", "22");
        return "/demo/view";
    }

    // 响应JSON数据（异步请求）
    // Java 对象 -> JSON 字符串 -> JS 对象

    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody//不加此注解默认返回html
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "zhangsan");
        emp.put("age", 20);
        emp.put("salary", 8000);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> emps = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("age", 20);
        emp.put("salary", 8000);
        emp.put("name", "zhangsan1");
        emps.add(emp);
        emp.put("name", "zhangsan2");
        emps.add(emp);
        emp.put("name", "zhangsan3");
        emps.add(emp);
        return emps;
    }

    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置cookie有效范围
        cookie.setPath("/community/alpha");
        // 设置过期时间 秒
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);
        return "set cookie successfully";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
//    public String getCookie(HttpServletRequest request) {
//        request.getCookies();
//        return "get cookie";
//    }
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("name", "test");
        session.setAttribute("id", "1");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("name"));
        System.out.println(session.getAttribute("id"));
        return "get session";
    }

}
