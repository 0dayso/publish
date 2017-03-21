package com.shinemo.publish.debug.websocket;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DebugSocketBootstrap {
	
	private static final AtomicBoolean startFlag = new AtomicBoolean();
	
	private static final Logger log = LoggerFactory.getLogger(DebugSocketBootstrap.class);
	
    @Resource
    private DebugSocketServer debugSocketServer;

    public void init() {
    	if(startFlag.compareAndSet(false, true)){
    		log.debug("debug socket start");
    		 new Thread() {
    	            @Override
    	            public void run() {
    	            	debugSocketServer.run();
    	            }
    	      }.start();
    	}else{
    		log.debug("debug socket has start");
    	}
    	
       
    }
}
