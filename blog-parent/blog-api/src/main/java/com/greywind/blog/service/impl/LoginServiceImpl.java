package com.greywind.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.greywind.blog.dao.pojo.SysUser;
import com.greywind.blog.service.LoginService;
import com.greywind.blog.service.SysUserService;
import com.greywind.blog.utils.JWTUtils;
import com.greywind.blog.vo.ErrorCode;
import com.greywind.blog.vo.Result;
import com.greywind.blog.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
//@Transactional //开启事务
public class LoginServiceImpl implements LoginService {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //加密盐
    private static final  String slat="GreyWind";

    /**
     * 登录功能
     *
     * @param loginParam
     * @return
     */
    @Override
    public Result login(LoginParam loginParam) {

        /**
         * 1.检查参数是否合法
         * 2.根据用户和密码去user中查询是否存在
         * 3.不存在登录失败
         * 4.如果存在，使用jwt生成token，返回前端
         * 5.token放入redis中，redis token:user信息 设置过期时间
         * 先人质token字符串是否合法，去redis认证是否存在
         */
        String account=loginParam.getAccount();
        String password=loginParam.getPassword();
        if(StringUtils.isBlank(account)||StringUtils.isBlank(password))
        {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }

        password= DigestUtils.md5Hex(password+slat);
        SysUser sysUser= sysUserService.findUser(account,password);
        if(sysUser==null)
        {
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        String token=JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }


    @Override
    public SysUser checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String,Object> stringObjectMap=JWTUtils.checkToken(token);
        if(stringObjectMap==null)
        {
            return null;
        }
        String userJson=redisTemplate.opsForValue().get("TOKEN_"+token);
        if(StringUtils.isBlank(userJson)){
            return null;
        }
        SysUser sysUser=JSON.parseObject(userJson,SysUser.class);
        System.out.println(sysUser);
        return sysUser;
    }

    /**
     * 退出登录
     *
     * @param token
     * @return
     */
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }

    /**
     * 注册
     *
     * @param loginParam
     * @return
     */
    @Override
    public Result register(LoginParam loginParam) {
        /**
         * 1判断参数是否合法
         * 2判断账户是否存在 存在 返回账户已经被注册
         * 3 不存在 注册用户
         * 4 生成token
         * 5 存入 redis 返回
         * 6 注意加上 事务 一旦中间的任何过程出现问题，注册的用户需要回滚
         */
        String account=loginParam.getAccount();
        String password=loginParam.getPassword();
        String nickname=loginParam.getNickname();

        if(StringUtils.isBlank(account)||StringUtils.isBlank(password)||StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        SysUser sysUser=sysUserService.findUserByAccount(account);
        if (sysUser!=null)
        {
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        sysUser=new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("");
        sysUser.setAdmin(1);
        sysUser.setDeleted(0);
        sysUser.setSalt("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);

        String token=JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token,JSON.toJSONString(sysUser),1,TimeUnit.DAYS);

        return Result.success(token);
    }
}
