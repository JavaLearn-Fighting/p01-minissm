package com.bjpowernode.service;

import com.bjpowernode.pojo.Admin;

public interface AdminService {
    /**
     * 登录验证函数
     * @param name 用户名
     * @param pwd 密码
     * @return 用户对象
     */
    Admin login(String name,String pwd);
}
