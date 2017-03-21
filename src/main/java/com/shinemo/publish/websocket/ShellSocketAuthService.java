package com.shinemo.publish.websocket;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.redis.service.RedisService;
import com.shinemo.publish.redis.util.RedisKey;
import com.shinemo.publish.utils.Requests;
import com.shinemo.publish.utils.UserUtil;


public class ShellSocketAuthService {
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private RedisService redisService;


    /**
     * 登录检查
     *
     * @param sid 会话ID
     * @return
     */
    public boolean loginCheck(String sid) {
        String namespace = RedisKey.getSessionKey(sid);
        String uid = redisService.hget(6, namespace, "userId", String.class);
        long uid_l = UserUtil.getUid(Requests.getRequest());
        if(uid.equals(String.valueOf(uid_l))){
        	logger.error("uid not valid! " +uid +" or "+ uid_l);
        	return false;
        }
        boolean flag = StringUtils.isNotBlank(uid) && NumberUtils.toLong(uid) > 0;
        logger.info("hive web socket login result:{},sid:{},uid:{}", flag, sid, uid);
        return flag;
    }
}
