package com.msds.redis.annation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName:RedisFieldNotCache.java
 * @Description: 某些字段不用缓存
 * @author gaoguangjin
 * @Date 2015-5-20 上午12:33:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RedisFieldNotCache {
}
