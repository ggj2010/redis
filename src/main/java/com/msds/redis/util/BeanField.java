package com.msds.redis.util;

import java.lang.reflect.Field;

/**
 * @ClassName:BeanField.java
 * @Description: bean工具类
 * @author gaoguangjin
 * @Date 2015-5-25 下午2:30:58
 */
public class BeanField {
	String primaryKey;
	String className;
	Field[] fieldList;
	
	public BeanField(String primaryKey, String className, Field[] fieldList) {
		this.primaryKey = primaryKey;
		this.className = className;
		this.fieldList = fieldList;
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}
	
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public Field[] getFieldList() {
		return fieldList;
	}
	
	public void setFieldList(Field[] fieldList) {
		this.fieldList = fieldList;
	}
}
