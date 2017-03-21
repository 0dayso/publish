package com.shinemo.publish.debug.websocket;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.redis.service.RedisService;
import com.shinemo.publish.redis.util.RedisKey;
import com.shinemo.publish.service.UsersService;


public class DebugSocketAuthService {
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private RedisService redisService;
    @Resource
    private UsersService usersService;


    /**
     * 登录检查
     *
     * @param sid 会话ID
     * @return
     */
    public boolean loginCheck(String sid) {
        String namespace = RedisKey.getSessionKey(sid);
        String uid = redisService.hget(6, namespace, "userId", String.class);
        
        
        
        boolean flag = StringUtils.isNotBlank(uid) && NumberUtils.toLong(uid) > 0;
        logger.info("debug web socket login result:{},sid:{},uid:{}", flag, sid, uid);
        
        //TODO 判断是否系统用户
        try {
        	 if(flag){
     	        UsersQuery query = new UsersQuery();
     	        query.setSsoUserId(Long.parseLong(uid));
     	        Result<Users> r = usersService.get(query);
     	        if(r == null || !r.isSuccess() ||r.isEmpty() ||r.getValue()==null){
     	        	logger.error("usersService.get() is null.  uid = "+uid);
     	        }
     	        logger.error("usersService.get() "+uid);
             }
		} catch (Exception e) {
			e.printStackTrace();
		}
       
        return flag;
    }
}
