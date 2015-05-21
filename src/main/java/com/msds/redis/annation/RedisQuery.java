package com.msds.redis.annation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName:Query.java
 * @Description: 查询条件 需要缓存到redis
 * @author gaoguangjin
 * @Date 2015-5-19 下午10:10:17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RedisQuery {
}
