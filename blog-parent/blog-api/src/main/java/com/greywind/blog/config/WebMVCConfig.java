package com.greywind.blog.config;

import com.greywind.blog.common.JackSonObjectMapper;
import com.greywind.blog.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截test接口，后序实际遇到配置为真正的拦截接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test")
                .addPathPatterns("/comments/create/change")
                .addPathPatterns("/articles/publish");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨区配置

//        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
        registry.addMapping("/**")
                //.allowedOrigins("http://www.greywind.cn")
                .allowedOriginPatterns("*")
                .allowedMethods("POST","GET","PUT","OPTIONS","DELETE")
                .maxAge(3600)
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowCredentials(true);


    }



    //扩展mvc框架的消息转换器
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JackSonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0, messageConverter);
    }

}
