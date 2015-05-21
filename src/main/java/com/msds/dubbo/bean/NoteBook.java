package com.msds.dubbo.bean;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.msds.redis.annation.RedisCache;
import com.msds.redis.annation.RedisFieldNotCache;
import com.msds.redis.annation.RedisQuery;

/**
 * @ClassName:NoteBook.java
 * @Description: 笔记本实体类
 * @author gaoguangjin
 * @Date 2015-5-19 下午10:18:23
 */
@Getter
@Setter
@RedisCache
public class NoteBook implements Serializable {
	
	@RedisFieldNotCache
	private static final long serialVersionUID = 1L;
	@RedisFieldNotCache
	private static final String className = "NoteBook";
	@RedisFieldNotCache
	private static final String primaryKey = "noteBookId";
	
	private int noteBookId;
	@RedisQuery
	private String noteBookName;
	private int textSum;// 统计该笔记本下面有多少文本
	private NoteBookGroup noteBookGroup;
	@RedisQuery
	private Integer flag;
	@RedisQuery
	private Date createdate;
}
