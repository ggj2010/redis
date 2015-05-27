package com.msds.redis.performance;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

/**
 * @ClassName:RedisEasyTest.java
 * @Description: redis性能测试。10万条数据
 * @see:1、用原生态的jedis 最慢！！！！！！
 * @see:2、用事物transaction 33
 * @see:3、用pipelined 9秒
 * @see:4、用pipelined 里面开启事物 效率和 transaction一样
 * @author gaoguangjin
 * @Date 2015-5-27 下午4:33:21
 */
@Slf4j
public class RedisEasyTest {
	private static Jedis jedis = new Jedis("10.1.9.231");
	private static Jedis jedis2 = new Jedis("10.1.9.231");
	private static Jedis jedis3 = new Jedis("10.1.9.231");
	private static Pipeline p = jedis.pipelined();
	
	// 10万条数据
	private static int KEY_COUNT_1 = 100000;
	private static int KEY_COUNT_2 = 200000;
	private static int KEY_COUNT_3 = 300000;
	private static int KEY_COUNT_4 = 400000;
	
	public static void main(String[] args) {
		jedis.auth("msds");
		jedis.flushDB();
		
		jedis2.auth("msds");
		Transaction transaction = jedis2.multi();
		
		jedis3.auth("msds");
		Pipeline pipeline = jedis3.pipelined();
		pipeline.multi();
		
		// 不要把注释打开，否则慢的要死！！！
		long start = System.currentTimeMillis();
		// jedis();
		// System.out.printf("jedis use %d sec \n", (System.currentTimeMillis() - start) / 1000);
		
		start = System.currentTimeMillis();
		transation(transaction);
		System.out.printf("transation use %d sec \n", (System.currentTimeMillis() - start) / 1000);
		
		start = System.currentTimeMillis();
		piple();
		System.out.printf("batch piple use %d sec \n", (System.currentTimeMillis() - start) / 1000);
		
		// start = System.currentTimeMillis();
		// pipleWithTransation(pipeline);
		// System.out.printf("batch piple transation use %d sec \n", (System.currentTimeMillis() - start) / 1000);
		
	}
	
	private static void pipleWithTransation(Pipeline pipeline) {
		for (int i = KEY_COUNT_3; i < KEY_COUNT_4; i++) {
			pipeline.set("pipletransation" + i, i + "");
		}
		pipeline.exec();
	}
	
	private static void piple() {
		for (int i = KEY_COUNT_2; i < KEY_COUNT_3; i++) {
			p.set("piple" + i, i + "");
		}
		p.sync();
	}
	
	private static void transation(Transaction transaction) {
		for (int i = KEY_COUNT_1; i < KEY_COUNT_2; i++) {
			transaction.set("piple" + i, i + "");
		}
		transaction.exec();
		
	}
	
	private static void jedis() {
		for (int i = 0; i < KEY_COUNT_1; i++) {
			jedis.set("normal" + i, i + "");
		}
		
	}
	
}
