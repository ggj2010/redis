package com.msds.redis.annation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName:Cache.java
 * @Description: 需要缓存到redis实体类
 * @author gaoguangjin
 * @Date 2015-5-19 下午10:09:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RedisCache {
	public boolean need() default true;
}
