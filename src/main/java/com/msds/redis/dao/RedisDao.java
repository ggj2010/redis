package com.msds.redis.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.alibaba.fastjson.JSON;
import com.msds.dubbo.common.BeanField;
import com.msds.redis.annation.RedisFieldNotCache;
import com.msds.redis.annation.RedisQuery;

/**
 * @ClassName:RedisDao.java
 * @Description: redisDao公共API
 * @author gaoguangjin
 * @Date 2015-5-19 下午11:27:29
 */
public class RedisDao {
	private final static String SPLIT_MARK = ":";
	private final static String SORT = "sort";
	private final static String INDEX = "index";
	private final static String LOG = "log";
	// pub/sub模式打印log
	public final static String PUB_LOG = "publog";
	private static Jedis jedis;
	private static Transaction transaction;
	
	public RedisDao(Transaction transaction) {
		this.transaction = transaction;
	}
	
	/**
	 * 用不到事物
	 */
	public RedisDao(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * @Description比较key存储的value 和传入的value是否相等
	 * @param key
	 * @return:Boolean
	 */
	public static Boolean existValueByKey(String key, String value) {
		return jedis.get(key) == value ? true : false;
	}
	
	/**
	 * @Description: 删除string类型数据
	 * @param key
	 */
	public void delString(String key) {
		transaction.del(key);
	}
	
	/**
	 * @Description: 删除set类型数据
	 * @param key
	 */
	public void delSet(String key, String member) {
		transaction.srem(key, member);
	}
	
	/**
	 * @Description:模糊查询key值
	 * @param pattern
	 * @return:Set<String>
	 */
	public static Set<String> keys(String pattern) {
		return jedis.keys(pattern);
	}
	
	/**
	 * @Description:根据key值返回set集合
	 * @param key
	 * @return:Set<String>
	 */
	public static Set<String> smembers(String key) {
		return jedis.smembers(key);
	}
	
	/**
	 * @Description:初始化将表中数据放到redis,存放格式为tableName:id:column.
	 * @param tableName 表映射的类名
	 * @param id 主键ID值
	 * @param column 表映射的列信息
	 * @param value 列对应的值
	 */
	public void setTable(String tableName, String id, String column, String value) {
		transaction.set(tableName + SPLIT_MARK + id + SPLIT_MARK + column, value);
	}
	
	/**
	 * @Description:初始化存放表中所有字段数据，存放格式为tableName:column:columnValue
	 * @param tableName
	 * @param column
	 * @param value
	 * @param id
	 */
	public void saddColumn(String tableName, String column, String columnValue, String id) {
		transaction.sadd(tableName + SPLIT_MARK + column + SPLIT_MARK + columnValue, tableName + SPLIT_MARK + id);
	}
	
	/**
	 * @Description:初始化将表中数据放到redis,将bean转换成json格式，存放格式为tableName:id。
	 * @param tableName 表映射的类名
	 * @param id 主键id值
	 * @param json 改id值对应的json字符串
	 */
	public void setJSON(String tableName, String id, String json) {
		transaction.set(tableName + SPLIT_MARK + id, json);
	}
	
	/**
	 * @Description: 根据jsonKey，获取相应的json字符串，转换成实体类List
	 * @param key
	 * @return
	 * @return:List<T>
	 */
	public static List<?> getListBean(Set<String> sortKey, Class classs, Jedis jedis) {
		List<Object> list = new ArrayList<Object>();
		for (String key : sortKey) {
			list.add(getBean(key, classs, jedis));
		}
		return list;
	}
	
	/**
	 * @Description:根据jsonKey，获取相应的json字符串，转换成实体类
	 * @param key
	 * @param classs实体类
	 * @return:T dao层泛型的实体类
	 */
	public static Object getBean(String key, Class classs, Jedis jedis) {
		return JSON.parseObject(jedis.get(key), classs);
	}
	
	/**
	 * @Description:对指定值加排序
	 * @param tableName
	 * @param column
	 * @param value
	 * @param id
	 * @return:void
	 */
	public void zaddSort(String tableName, String column, String value, String id) {
		transaction.zadd(tableName + SPLIT_MARK + SORT + SPLIT_MARK + column, Double.parseDouble(value), id);
	}
	
	/**
	 * @Description:对表主键加索引
	 * @param tableName
	 * @param column
	 * @param value
	 * @return:Long
	 */
	public void zaddIndex(String tableName, String column, String value) {
		transaction.sadd(tableName + SPLIT_MARK + INDEX + SPLIT_MARK + column, tableName + SPLIT_MARK + value);
	}
	
	/**
	 * @Description:操作redis日志转换成对应的sql
	 * @param value
	 * @return:Long
	 */
	public void log(String sql) {
		// set里面放类名+json list里面放类名
		transaction.lpush(LOG, sql);
	}
	
	public void watch(String... key) {
		
	}
	
	/**
	 * @Description: 获取实体类对象list 插入到redis里面
	 * @param list 实体类集合
	 */
	public void insertListToredis(List<Object> list) throws Exception {
		if (null != list && list.size() > 0) {
			BeanField bf = getBeanField(list.get(0));
			for (Object tt : list) {
				insertSingleDataToredis(tt, bf);
			}
		}
	}
	
	/**
	 * @Description:插入单个实体类到redis
	 * @param t 实体类
	 * @param fieldList 实体类字段数组
	 * @param primaryKey 实体类主键的名称
	 * @param className 实体类的名称
	 */
	public void insertSingleDataToredis(Object t, BeanField bf) throws Exception {
		Field[] fieldList = bf.getFieldList();
		String primaryKey = bf.getPrimaryKey();
		String className = bf.getClassName();
		// 获取主键值
		Field pvField = t.getClass().getDeclaredField(primaryKey);
		pvField.setAccessible(true);
		String primaryValue = pvField.get(t).toString();
		
		for (Field field : fieldList) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object fieldValue = field.get(t);
			// 去除不缓存的
			if (null != fieldValue && !field.isAnnotationPresent(RedisFieldNotCache.class)) {
				// 1、类型一k/v
				setTable(className, primaryValue, fieldName, fieldValue.toString());
				// 对有注解的进行sadd kv存储
				if (field.isAnnotationPresent(RedisQuery.class)) {
					// 2、类型二 k/v
					saddColumn(className, fieldName, fieldValue.toString(), primaryValue);
				}
			}
		}
		// 3、 存放映射bean key-jsonValue
		setJSON(className, primaryValue, JSON.toJSON(t).toString());
		// 4、所有主键建立索引
		zaddIndex(className, primaryKey, primaryValue);
	}
	
	/**
	 * @Description: 从redis删除条数据
	 * @param list
	 * @throws Exception
	 * @return:void
	 */
	public void delDataListFromRedis(List<Object> list) throws Exception {
		if (null != list && list.size() > 0) {
			// 例如 primaryKey=noteId className=Note
			BeanField bf = getBeanField(list.get(0));
			for (Object tt : list) {
				delSingleDataFromRedis(tt, bf);
			}
		}
	}
	
	public BeanField getBeanField(Object t) throws Exception {
		// 获取主键名称
		Field pkField = t.getClass().getDeclaredField("primaryKey");
		pkField.setAccessible(true);
		String primaryKey = pkField.get(t).toString();
		
		// 获取类名称
		Field cnField = t.getClass().getDeclaredField("className");
		cnField.setAccessible(true);
		String className = cnField.get(t).toString();
		
		Field[] fieldList = t.getClass().getDeclaredFields();
		return new BeanField(primaryKey, className, fieldList);
	}
	
	/**
	 * @Description: 从redis里面删除某一条数据
	 * @param t
	 * @param fieldList
	 * @param primaryKey
	 * @param className
	 * @throws Exception
	 * @return:void
	 */
	public void delSingleDataFromRedis(Object t, BeanField bf) throws Exception {
		Field[] fieldList = bf.getFieldList();
		String primaryKey = bf.getPrimaryKey();
		String className = bf.getClassName();
		
		// 获取主键值
		Field pvField = t.getClass().getDeclaredField(primaryKey);
		pvField.setAccessible(true);
		String primaryValue = pvField.get(t).toString();
		for (Field field : fieldList) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object fieldValue = field.get(t);
			// 去除不缓存的
			if (null != fieldValue && !field.isAnnotationPresent(RedisFieldNotCache.class)) {
				// 1、删除类型一k/v
				delString(className + SPLIT_MARK + primaryValue + SPLIT_MARK + fieldName);
				
				// 对有注解的进行sadd kv存储
				if (field.isAnnotationPresent(RedisQuery.class)) {
					// 2、删除类型2
					delSet(className + SPLIT_MARK + fieldName + SPLIT_MARK + fieldValue.toString(), className + SPLIT_MARK + primaryValue);
				}
			}
		}
		// 3、删除类型三 json格式
		delString(className + SPLIT_MARK + primaryValue);
		// 4、删除索引
		delSet(className + SPLIT_MARK + INDEX + SPLIT_MARK + primaryKey, className + SPLIT_MARK + primaryValue);
	}
	
	/**
	 * @Description:pub/sub模式广播sqllog
	 * @param log
	 * @return:void
	 */
	public void pubish(String log) {
		transaction.publish(LOG, log);
	}
	
}
