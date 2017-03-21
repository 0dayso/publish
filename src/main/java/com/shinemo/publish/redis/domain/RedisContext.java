package com.shinemo.publish.redis.domain;

public interface RedisContext {

	int REDIS_TIMEOUT = 2000;
    int REDIS_MAX_TOTAL = 8;
    int REDIS_MAX_IDLE = 4;
    int REDIS_MIN_IDLE = 1;
    int REDIS_MAX_WAITMILLIS = 5000;
    int REDIS_MIN_EVICTABLEIDLETIMEMILLIS = 300000;
    int REDIS_NUMTESTSPEREVICTIONRUN = 3;
    int REDIS_TIMEBETWEENEVICTIONRUNMILLIS = 60000;
    boolean REDIS_TESTONBORROW = false;
    boolean REDIS_TESTONRETURN = false;
    boolean REDIS_TESTWHILEIDLE = false;
	
}
