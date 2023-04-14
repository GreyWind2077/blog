package com.greywind.blog.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigInteger;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;


public class JackSonObjectMapper extends ObjectMapper {
    public JackSonObjectMapper() {
        super();
        //收到未知属性时不报异常
        this.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        //反序列化时，属性不存在的兼容处理
        this.getDeserializationConfig().withoutFeatures(FAIL_ON_UNKNOWN_PROPERTIES);

        //自定义转换规则 处理ID精度失真问题 将Long类型转换成JSON的时候转成String类型
        SimpleModule simpleModule = new SimpleModule()
                .addSerializer(BigInteger.class, ToStringSerializer.instance)//将BigInteger转换为String
                .addSerializer(Long.class, ToStringSerializer.instance);//将Long转换成String
        this.registerModule(simpleModule);
    }

}
