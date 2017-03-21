package com.shinemo.publish.redis.util;

public class RedisKey {
	
	public static final String SESSION_PREFIX = "sm_s_%s";
	
	public static final String VERIFY_CODE = "sm_vc";
	
	public static final String USER_PREFIX = "sm_u_%s";
	
	public static String getSessionKey(String sessionId){
		return String.format(SESSION_PREFIX, sessionId);
	}
	
	public static String getVerifyCode(){
		return VERIFY_CODE;
	}
	
	//用户列表
	public static String getUserKey(String userId){
		return String.format(USER_PREFIX, userId);
	}
	

}
