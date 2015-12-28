package com.msds.redis.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @ClassName:RedisCacheManagers.java
 * @Description: redisMap 封装不同的db
 * @author gaoguangjin
 * @Date 2015-5-27 下午7:49:54
 */
public class RedisCacheManagers {
	private static String redisdbtype;
	private static String redisdbnumber;

	private static String host;
	private static String port;
	private static String timeout;
	private static String passwords;

	private static String maxtotal;
	private static String maxidle;
	private static String minidle;
	private static String maxwaitmillis;
	private static String testonborrow;
	private static String testwhileidle;

	private static JedisPoolConfig poolConfig = null;

	// 保存不同的数据库连接
	private static ConcurrentHashMap<String, RedisCachePool> redisPoolMap = new ConcurrentHashMap<String, RedisCachePool>();

	static {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("redis.properties");
		InputStream is;
		try {
			Properties props = new Properties();
			is = resource.getInputStream();
			props.load(is);

			redisdbtype = props.getProperty("redisdbtype");
			redisdbnumber = props.getProperty("redisdbnumber");
			host = props.getProperty("host");
			port = props.getProperty("port");
			timeout = props.getProperty("timeout");
			passwords = props.getProperty("passwords");
			maxtotal = props.getProperty("maxtotal");
			maxidle = props.getProperty("maxidle");
			minidle = props.getProperty("minidle");
			maxwaitmillis = props.getProperty("maxwaitmillis");
			testonborrow = props.getProperty("testonborrow");
			testwhileidle = props.getProperty("testwhileidle");
		} catch (IOException e) {
			System.out.println("初始化redis连接池失败！");
		}

	}

	/**
	 * @Description: 手动测试
	 * @param args
	 * @return:void
	 */
	public static void main(String[] args) {
		// ApplicationContext ac = new
		// ClassPathXmlApplicationContext("classpath:spring-context.xml");
	}

	public static ConcurrentHashMap<String, RedisCachePool> getRedisPoolMap() {
		if (redisPoolMap.size() < 1) {
			initConfig();
			initPoolMap();
		}
		return redisPoolMap;
	}

	/**
	 * @Description:共享的poolconfig
	 * @return:void
	 */
	private static void initConfig() {
		poolConfig = new JedisPoolConfig();
		poolConfig.setTestOnBorrow(testwhileidle.equals("true") ? true : false);
		poolConfig.setTestWhileIdle(testonborrow.equals("true") ? true : false);
		poolConfig.setMaxIdle(Integer.parseInt(maxidle));
		poolConfig.setMaxTotal(Integer.parseInt(maxtotal));
		poolConfig.setMinIdle(Integer.parseInt(minidle));
		poolConfig.setMaxWaitMillis(Integer.parseInt(maxwaitmillis));
	}

	private static void initPoolMap() {
		try {
			if (null != redisdbtype && null != redisdbnumber) {
				String[] dbs = redisdbtype.split(",");
				String[] numbers = redisdbnumber.split(",");
				for (int i = 0; i < dbs.length; i++) {
					// 得到redis连接池对象
					JedisPool jedisPool = new JedisPool(poolConfig, host, Integer.parseInt(port),
							Integer.parseInt(timeout), passwords);
					// 存放不同redis数据库
					redisPoolMap.put(dbs[i], new RedisCachePool(Integer.parseInt(numbers[i]), jedisPool));
				}
			}
		} catch (Exception e) {
			// log.error("redisCacheManager初始化失败！" + e.getLocalizedMessage());
		}
	}

	/**
	 * @Description: 得到jedis连接
	 * @param dbtypeName
	 * @return:Jedis
	 */
	public Jedis getResource(RedisDataBaseType dbtypeName) {
		Jedis jedisResource = null;
		RedisCachePool pool = redisPoolMap.get(dbtypeName.toString());
		if (pool != null) {
			jedisResource = pool.getResource();
		}
		return jedisResource;
	}

	/**
	 * @Description: 返回连接池
	 * @param dbtypeName
	 * @param jedis
	 * @return:void
	 */
	public void returnResource(RedisDataBaseType dbtypeName, Jedis jedis) {
		RedisCachePool pool = redisPoolMap.get(dbtypeName.toString());
		if (pool != null)
			pool.returnResource(jedis);
	}
}
