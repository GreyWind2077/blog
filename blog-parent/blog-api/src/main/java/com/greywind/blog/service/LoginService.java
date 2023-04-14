package com.greywind.blog.service;

import com.greywind.blog.dao.pojo.SysUser;
import com.greywind.blog.vo.Result;
import com.greywind.blog.vo.params.LoginParam;
import org.springframework.transaction.annotation.Transactional;

@Transactional //启动事务
public interface LoginService {
    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    /**
     * 退出登录
     *
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册
     * @param loginParam
     * @return
     */
    Result register(LoginParam loginParam);
}
