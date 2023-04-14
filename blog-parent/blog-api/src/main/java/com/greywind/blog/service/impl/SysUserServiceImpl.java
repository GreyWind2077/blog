package com.greywind.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.greywind.blog.dao.mapper.SysUserMapper;
import com.greywind.blog.dao.pojo.SysUser;
import com.greywind.blog.service.LoginService;
import com.greywind.blog.service.SysUserService;
import com.greywind.blog.vo.ErrorCode;
import com.greywind.blog.vo.LoginUserVo;
import com.greywind.blog.vo.Result;
import com.greywind.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private LoginService loginService;


    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser=sysUserMapper.selectById(id);

        if(sysUser==null)
        {
            sysUser=new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/a.png");
            sysUser.setNickname("马");
        }
        UserVo userVo=new UserVo();
        userVo.setAvatar(sysUser.getAvatar());
        userVo.setNickname(sysUser.getNickname());
        userVo.setId(sysUser.getId());
        return userVo;
    }

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser=sysUserMapper.selectById(id);

        if(sysUser==null)
        {
            sysUser=new SysUser();
            sysUser.setNickname("马");

        }
        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        //查询结果没有带id ....
        queryWrapper.select(SysUser::getId,SysUser::getAccount,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1");

        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据token查询用户信息
     *
     * @param token
     * @return
     */
    @Override
    public Result findUserByToken(String token) {
        /**
         * 1.token合法性校验 是否为空 redis是否成功
         * 2.如果失败 返回错误
         * 3.成功返回对应结果 loginUserVo
         */

        SysUser sysUser=loginService.checkToken(token);
        if(sysUser==null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }

        LoginUserVo loginUserVo=new LoginUserVo();
        loginUserVo.setId(sysUser.getId());
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());

        return Result.success(loginUserVo);
    }

    /**
     * 根据账户查找用户
     *
     * @param account
     * @return
     */
    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.last("limit 1");
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 保存用户
     *
     * @param sysUser
     */
    @Override
    public void save(SysUser sysUser) {

        //保存用户 id会自动生成
        //默认生成id 是分布式id 雪花算法
        //mybatis-plus
        this.sysUserMapper.insert(sysUser);
    }
}
