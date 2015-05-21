package com.msds.dubbo.common;

import java.lang.reflect.Field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeanField {
	String primaryKey;
	String className;
	Field[] fieldList;
	
	public BeanField(String primaryKey, String className, Field[] fieldList) {
		this.primaryKey = primaryKey;
		this.className = className;
		this.fieldList = fieldList;
	}
}
