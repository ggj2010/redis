package com.msds.redis.synchronize;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName:RedisUpdateToDataBase.java
 * @Description: redis异步更新到数据库
 * @author gaoguangjin
 * @Date 2015-5-22 上午10:09:48
 */
@Service
@Slf4j
@Transactional
public class RedisUpdateToDataBase {
	@Autowired
	SessionFactory sessionFactory;
	
	public boolean excuteUpdate(List<String> list) {
		try {
			Session session = sessionFactory.getCurrentSession();
			for (String string : list) {
				session.createSQLQuery(string).executeUpdate();
			}
			log.info("redis更新到数据库成功！");
			return true;
		} catch (Exception e) {
			log.info("redis更新到数据库失败！" + e.getLocalizedMessage());
		}
		return false;
	}
}
