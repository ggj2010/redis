package com.msds.test;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName:InitDubborProvider.java
 * @Description: 启动dubbo服务
 * @author gaoguangjin
 * @Date 2015-5-20 下午12:56:10
 */
@Slf4j
public class InitDubborProvider {
	@Test
	// 发布服务
	public void provider() throws IOException {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "dubbo-provider.xml" });
		context.start();
		// System.in.read(); // 为保证服务一直开着，利用输入流的阻塞来模拟
		// ApplicationContext ac = new ClassPathXmlApplicationContext("dubbo/dubbo-provider.xml");
	}
	
}
