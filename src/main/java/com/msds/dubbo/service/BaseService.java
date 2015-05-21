package com.msds.dubbo.service;

import java.util.List;

public interface BaseService<T> {
	List<T> findAll();
	
	void delete(String id);
}
