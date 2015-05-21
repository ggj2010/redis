package com.msds.dubbo.service;

import java.util.List;

public interface BaseService<T> {
	/* 查询所有 */
	public List<T> findAll();
	
	/* 删除 */
	void delete(String id);
	
	/* 更新 */
	void update(T t);
}
