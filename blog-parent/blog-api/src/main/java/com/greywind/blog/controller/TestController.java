package com.greywind.blog.controller;

import com.greywind.blog.dao.pojo.SysUser;
import com.greywind.blog.utils.UserThreadLocal;
import com.greywind.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {
    @RequestMapping
    private Result test(){
        SysUser sysUser= UserThreadLocal.get();
        System.out.println(sysUser);
        return Result.success(null);
    }
}
