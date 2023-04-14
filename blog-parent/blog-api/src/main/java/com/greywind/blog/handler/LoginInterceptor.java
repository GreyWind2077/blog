package com.greywind.blog.handler;


import com.alibaba.fastjson.JSON;
import com.greywind.blog.dao.pojo.SysUser;
import com.greywind.blog.service.LoginService;
import com.greywind.blog.utils.UserThreadLocal;
import com.greywind.blog.vo.ErrorCode;
import com.greywind.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {





        //在执行controller方法之前执行执行
        /**
         * 1.需要判断 请求接口路径是否为HandlerMenthod
         * 2.判断token是否为空 为登录
         * 3.如果token 不为空 验证loginservice checkToken
         * 4.验证成功 放行即可
         */
        if(!(handler instanceof HandlerMethod)){
            //handler可能是RequestResourceHandler springboot程序 访问静态资源 默认去classpath下的static目录去寻找
            return true;
        }

        String token=request.getHeader("Authorization");
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if(StringUtils.isBlank(token))
        {
            Result result=Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser=loginService.checkToken(token);
        if(sysUser==null){
            Result result=Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        //登录验证成功，放行
        //我希望在controller中 直接获取用户信息 怎么获取

        UserThreadLocal.put(sysUser);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //如果不删除ThreadLocal中用完的信息，会有内存泄漏的危险
        UserThreadLocal.remove();
    }
}
