# 项目中遇到的各种问题

1.数据库无法连接

​	解决方案为将application.propertes文件改为

​	application.yml，用yml配置文件即可成功连接数据库

2.时间问题 

项目采用bigint存放时间即毫秒单位的一个值

不能直接year(creat_time) as year 而要使用year(FROM_UNIXTIME(creat_time/1000)) as year



3.阅读次数bug

由于在pojo中评论数设阅读数为int类型，导致update语句执行时将参数带入语句中

要将所有类型变为包装类型，而不是使用基本类型



4.类型对应问题

错误和3类似，由于article类中全部为包装类型，而articleVo中还存在基本类型，导致使用BeanUtils.copyProperties()时类型不匹配发生错误



5.id精确度问题

数据库所有表中的id生成均采用分布式id的方式（即用雪花算法生成id），前端读取时候会出现精度问题，导致id数据出现错误，解决方法为在所有Vo类中在id属性上添加

```java
@JsonSerialize(using = ToStringSerializer.class)
```



6.id进度问题的其他解决方法

和5是面对的是同一个问题但是还有其他解决方法

方法1 将和前端交互的Vo类中id 的属性转变为String类型，然后再service层中将String和long类型相互转换即可

方法2 （我在用的方法，但是可能会有问题）

在webmvcconfig中配置消息装换器，将long类型变为json时将其变为string类型

~~~java

//转换器的实现类
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
~~~



~~~java
//扩展mvc框架的消息转换器
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JackSonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0, messageConverter);
    }
~~~



这样使用固然方便，但会导致使用时将不需要转换的类型被转换，出现某些难以察觉的错误





# 项目提示

1 加密盐 即对秘钥进行加密后再加上一段字符串，该串秘钥被称为盐，也可以视为公司内部秘钥。

2.threadlocal内存泄漏 

![image-20221205105905042](C:\Users\asus\AppData\Roaming\Typora\typora-user-images\image-20221205105905042.png)

**实线代表强引用,虚线代表弱引用**

每一个Thread维护一个ThreadLocalMap, key为使用**弱引用**的ThreadLocal实例，value为线程变量的副本。

**强引用**，使用最普遍的引用，一个对象具有强引用，不会被垃圾回收器回收。当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，也不回收这种对象。

**如果想取消强引用和某个对象之间的关联，可以显式地将引用赋值为null，这样可以使JVM在合适的时间就会回收该对象。**

**弱引用**，JVM进行垃圾回收时，无论内存是否充足，都会回收被弱引用关联的对象。在java中，用java.lang.ref.WeakReference类来表示



3线程池

更新阅读数量时，将更新操作都放进线程池中去操作，从而不影响主线程的使用，提高资源的利益率



4.sql语句条件让线程更安全

更新评论时增加一个条件

~~~java
updateWrapper.eq(Article::getViewCounts,viewCounts);
//update article set view_count=100 where view=99 and id=11
~~~



查看当前数据是否为更新前的数据，然后进行更新

避免数据出错 ：如将评论从100更新为101时，如果有其他人更新表中数据为110,不加条件就直接更新表将数据变为101，导致减少了10个评论数。





### 缓存一致性问题

读取最新文章接口时采用了缓存，但是加入缓存会存在一些问题，当修改或者用户流量文章时，最新的修改和文章的浏览数量无法即时更新

采用RocketMQ来解决这个问题

采用队列 ，修改消息后放到里面，通过队列修改







未来优化

1. 文章可以放入es当中，便于后续中文分词搜索。springboot教程有和es的整合
2. 评论数据，可以考虑放入mongodb当中 电商系统当中 评论数据放入mongo中
3. 阅读数和评论数 ，考虑把阅读数和评论数 增加的时候 放入redis incr自增，使用定时任务 定时把数据固话到数据库当中
4. 为了加快访问速度，部署的时候，可以把图片，js，css等放入七牛云存储中，加快网站访问速度

做一个后台 用springsecurity 做一个权限系统，对工作帮助比较大

将域名注册，备案，部署相关



##  总结

1. jwt + redis

   token令牌的登录方式，访问认证速度快，session共享，安全性

   redis做了 令牌和 用户信息的对应管理，1. 进一步增加了安全性 2. 登录用户做了缓存 3. 灵活控制用户的过期（续期，踢掉线等）

2. threadLocal 使用了保存用户信息，请求的线程之内，可以随时获取登录的用户，做了线程隔离

3. 在使用完ThreadLocal之后，做了value的删除，防止了内存泄漏

4. 线程安全- update table set value = newValue where id=1 and value=oldValue

5. 线程池 应用非常广，面试 7个核心参数 （对当前的主业务流程 无影响的操作，放入线程池执行）

   1. 登录 ，记录日志

6. 权限系统 重点内容

7. 统一日志记录，统一缓存处理



部署相关

