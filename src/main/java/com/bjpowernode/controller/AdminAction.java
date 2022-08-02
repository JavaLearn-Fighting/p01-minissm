package com.bjpowernode.controller;


import com.bjpowernode.pojo.Admin;
import com.bjpowernode.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminAction {
    //引入业务逻辑层对象
    @Autowired
    AdminService adminService;

    //登录判断
    @RequestMapping("/login")
    public String login(String name, String pwd, HttpServletRequest request){
        Admin admin = adminService.login(name, pwd);
        if (admin != null){
            //登录成功
            request.setAttribute("admin",admin);
            return "main";
        }else{
            request.setAttribute("errmsg","用户名或密码错误！ ");
            return "login";
        }
    }
}
