package com.msds.redis.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import com.alibaba.fastjson.JSON;
import com.msds.redis.annation.RedisFieldNotCache;
import com.msds.redis.annation.RedisQuery;
import com.msds.redis.util.BeanField;

/**
 * @ClassName:RedisDao.java
 * @Description: redisDao公共API
 * @author gaoguangjin
 * @Date 2015-5-19 下午11:27:29
 */
public class RedisDao {
	// 分隔符
	private final static String SPLIT_MARK = ":";
	// 排序key里面的标记位
	private final static String SORT = "sort";
	// 主键key里面的标记位
	private final static String INDEX = "index";
	// list格式存放log的sql
	public final static String LOG = "log";
	// pub/sub模式打印log
	public final static String PUB_LOG = "publog";
	
	private static Jedis jedis;
	// 事物
	private static Transaction transaction;
	
	// 管道
	private static Pipeline pipeline;
	
	/**
	 * redis事物一旦开启之后，所有的命令都会存放都一个队里里面，不会立即执行。
	 */
	public RedisDao(Transaction transaction) {
		this.transaction = transaction;
	}
	
	/**
	 * 某些查询方法，用不到事物。
	 */
	public RedisDao(Jedis jedis) {
		this.jedis = jedis;
	}
	
	/**
	 * 管道+事物效率等于Transaction，目前没用到，如果某些操作不需要事物控制，可以用到管道
	 */
	public RedisDao(Pipeline pipeline) {
		this.pipeline = pipeline;
	}
	
	/**
	 * @Description比较key存储的value 和传入的value是否相等
	 * @param key
	 * @return:Boolean
	 */
	public Boolean existValueByKey(String key, String value) {
		return jedis.get(key) == value ? true : false;
	}
	
	/**
	 * @Description:根据key返回值
	 * @param key
	 * @param jedis
	 * @return:String
	 */
	public static String get(String key, Jedis jedis) {
		return jedis.get(key);
	}
	
	/**
	 * @Description: 返回有序集 key 中，指定区间内的成员。 按照从小到大排序
	 * @param key key值 Note:sort:noteId 0 -1
	 * @param start 初始位置
	 * @param end 结束位置
	 * @param jedis
	 * @return:Set<String>
	 */
	public static Set<String> getRangeSortSet(String key, int start, int end, Jedis jedis) {
		return jedis.zrange(key, start, end);
	}
	
	/**
	 * @Description: 返回有序集 key 中，指定区间内的成员。 按照从大到小排序
	 * @see: 例如用来插入数据时候获取主键的id
	 * @param key key值 Note:sort:noteId 0 0
	 * @param start 初始位置
	 * @param end 结束位置
	 * @param jedis
	 * @return:Set<String>
	 */
	public static Set<String> getRevrangeSortSet(String key, int start, int end, Jedis jedis) {
		return jedis.zrevrange(key, start, end);
	}
	
	/**
	 * @Description:根据key的set集合，返回多个key对应的stringList。
	 * @param sortKey
	 * @param jedis
	 * @return:List<String>
	 */
	public static List<String> getListString(Set<String> sortKey, Jedis jedis) {
		List<String> list = new ArrayList<String>();
		for (String key : sortKey) {
			list.add(jedis.get(key));
		}
		return list;
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
	 * @Description: 删除sortset类型的数据
	 * @param key
	 * @param member
	 */
	public void delSortSet(String key, String member) {
		transaction.zrem(key, member);
	}
	
	/**
	 * @Description:模糊查询key值
	 * @param pattern
	 * @return:Set<String>
	 */
	public Set<String> keys(String pattern) {
		return jedis.keys(pattern);
	}
	
	/**
	 * @Description:根据key值返回set集合
	 * @param key
	 * @return:Set<String>
	 */
	public Set<String> smembers(String key) {
		return jedis.smembers(key);
	}
	
	/**
	 * @Description:根据多个key值返回交集的id
	 * @param key
	 * @return:Set<String>
	 */
	public Set<String> sinter(String... key) {
		return jedis.sinter(key);
	}
	
	/**
	 * @Description:根据多个key值 返回并集的id
	 * @param key
	 * @return:Set<String>
	 */
	public Set<String> sunion(String... key) {
		return jedis.sunion(key);
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
		transaction.lpush(LOG, sql);
	}
	
	/**
	 * @Description: 监听必须在开启事物之前，执行watch命令
	 * @param keys
	 * @return:void
	 */
	public void watch(String... keys) {
		jedis.watch(keys);
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
		
		// 5、按照id大小排序
		zaddSort(className, primaryKey, primaryValue, primaryValue);
		
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
		// 5、删除某个排序
		delSortSet(className + SPLIT_MARK + SORT + SPLIT_MARK + primaryKey, primaryValue);
	}
	
	/**
	 * @Description:pub/sub模式广播sqllog
	 * @param log
	 * @return:void
	 */
	public void pubishLog(String log) {
		transaction.publish(PUB_LOG, log);
	}
	
	/**
	 * @Description:获取redis里面list类型的log。
	 * @see:lpop命令 返回并删除名称为key的list中的首元素。如果插入数据库失败需要再把log插入进来，调用log(sql)方法
	 * @return:String
	 */
	public static String lpopLog(Jedis jedis) {
		return jedis.lpop(LOG);
	}
	
	/**
	 * @Description:清空redis库所有缓存数据
	 * @return:void
	 */
	public void flushDB() {
		transaction.flushDB();
	}
	
	/**
	 * @Description: 更新redis里面的某个数据
	 * @param oldObject 原来的对象
	 * @param t
	 * @param bf
	 */
	public void updateSingleFromToredis(Object oldObject, Object t, BeanField bf)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
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
				// 1、更新类型一k/v
				setTable(className, primaryValue, fieldName, fieldValue.toString());
				// 对有注解的进行sadd kv存储
				if (field.isAnnotationPresent(RedisQuery.class)) {
					// 2、更新类型二 k/v
					saddColumn(className, fieldName, fieldValue.toString(), primaryValue);
					// 删除就的类型2
					delSet(className + SPLIT_MARK + fieldName + SPLIT_MARK + field.get(oldObject), className + SPLIT_MARK + primaryValue);
				}
			}
		}
		// 3、更新 存放映射bean key-jsonValue
		setJSON(className, primaryValue, JSON.toJSON(t).toString());
	}
	
}
