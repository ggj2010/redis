package com.msds.redis.test;


public class SysConstants {

	// --------------- redis pool ---------------
	
	// 从数据库连接池中取得连接时，对其的有效性进行检查
	public static final boolean TEST_ON_BORROW = true;
	
	// 最大连接数
	public static final int MAX_ACTIVE = 36;
	
	// 最大闲置的连接数
	public static final int MAX_IDLE = 20;
	
	// 最小.....
	public static final int MIN_IDLE = 5;
	
	// 请求最长等待时间/毫秒
	public static final int MAX_WAIT = 1000; 
	
	// 闲置时测试
	public static final boolean TEST_WHILE_IDLE = true;
	
	
}
