package com.msds.redis;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.msds.redis.test.NotifyDataBase;
import com.msds.redis.test.RedisPool;

@Slf4j
public class Demo {
	private static Jedis jedis;
	
	@Test
	public void init() {
		// 连接redis linux服务器
		jedis = new Jedis("123.56.118.135", 6379);
		// 权限认证
		jedis.auth("gaoguangjin");// 密码最好越长越好，防止暴力破解
		
	}
	
	public void tests() {
		
		// try {
		jedis.set("gao", "123");
		jedis.sadd("gao", "123");
		// } catch (Exception e) {
		// log.info("" + e.getLocalizedMessage());
		// }
		
		Transaction ts = jedis.multi();
		try {
			ts.set("gao", "123");
			Integer.parseInt("ddd");
			
			ts.exec();
		} catch (Exception e) {
			log.info("" + e.getLocalizedMessage());
			ts.discard();
		}
		
	}
	
	public static void main(String[] args) {
		// test();
		// select();// 查询用不到事物
		// update();// 用到事物
		
	}
	
	private static void update() {
		Jedis jediss = null;
		Transaction ts = null;
		try {
			jediss = RedisPool.getJedis();
			// 开启事物
			ts = jediss.multi();
			
			ts.set("t1", "1");
			ts.set("t2", "2");
			ts.set("t2", "2");
			List<Object> result = ts.exec();
			
			for (Object object : result)
				log.info(object.toString());
			
			RedisPool.returnResource(jediss);
		} catch (Exception e) {
			ts.discard();// 提交
		}
		// 如果用到事物，下部分代码不需要，因为在提交事物之后 会自动关闭连接的
		// finally {
		// // 关闭连接池
		// RedisPool.returnResource(jediss);
		// }
		
	}
	
	private static void select() {
		// TODO Auto-generated method stub
		
	}
	
	public static void test() {
		NotifyDataBase ndb = new NotifyDataBase();
		jedis.subscribe(ndb, "log");
	}
}
