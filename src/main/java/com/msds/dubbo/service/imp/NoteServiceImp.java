package com.msds.dubbo.service.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.msds.dubbo.bean.Note;
import com.msds.dubbo.service.BaseService;
import com.msds.dubbo.service.NoteService;
import com.msds.redis.dao.RedisDao;
import com.msds.redis.util.RedisCacheManager;
import com.msds.redis.util.RedisCachePool;
import com.msds.redis.util.RedisDataBaseType;

@Service("NoteServiceImp")
@Slf4j
public class NoteServiceImp implements NoteService, BaseService<Note> {
	@Autowired
	RedisCacheManager redisCacheManager;
	
	public List<Note> findAll() {
		List<Note> noteList = new ArrayList<Note>();
		RedisCachePool pool = null;
		Jedis jedis = null;
		try {
			pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
			jedis = pool.getResource();
			// 查询不用开启事物
			RedisDao rd = new RedisDao(jedis);
			Set<String> sortKey = rd.smembers("Note:index:noteId");
			noteList = (List<Note>) rd.getListBean(sortKey, Note.class, jedis);
		} catch (Exception e) {
			log.error(" List<Note> findAll()查询失败！" + e.getLocalizedMessage());
		}
		finally {
			log.info("回收jedis连接");
			pool.returnResource(jedis);
		}
		return noteList;
	}
	
	public void delete(String id) {
		RedisCachePool pool = null;
		Jedis jedis = null;
		RedisDao rd = null;
		try {
			pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
			jedis = pool.getResource();
			
			Object note = RedisDao.getBean("Note:" + id, Note.class, jedis);
			
			if (null != note) {
				// 查询之后开启事物
				Transaction transation = jedis.multi();
				
				rd = new RedisDao(transation);
				rd.delSingleDataFromRedis(note, rd.getBeanField(note));
				
				/* 处理之后的日志处理 */
				String logs = "delete from tcnote where note_id=" + id;
				rd.pubish(logs);
				rd.log(logs);
				
				transation.exec();
			}
		} catch (Exception e) {
			log.error(" delete(String id) 删除失败！" + e.getLocalizedMessage());
		}
		finally {
			log.info("回收jedis连接");
			pool.returnResource(jedis);
		}
	}
	
	public void update(Note bean) {
		RedisCachePool pool = null;
		Jedis jedis = null;
		RedisDao rd = null;
		pool = redisCacheManager.getRedisPoolMap().get(RedisDataBaseType.defaultType.toString());
		jedis = pool.getResource();
		
		Transaction transation = jedis.multi();
		
		rd = new RedisDao(transation);
		if (bean.getAuthorName() != null) {
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(" update tablName set ");
		
		if (bean.getAuthorName() != null) {
		}
		
	}
}
