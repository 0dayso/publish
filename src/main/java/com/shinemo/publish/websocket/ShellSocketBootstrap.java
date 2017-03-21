package com.shinemo.publish.websocket;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShellSocketBootstrap {
	
	private static final AtomicBoolean startFlag = new AtomicBoolean();
	
	private static final Logger log = LoggerFactory.getLogger(ShellSocketBootstrap.class);
	
    @Resource
    private ShellSocketServer shellSocketServer;

    public void init() {
    	if(startFlag.compareAndSet(false, true)){
    		log.debug("shell socket start");
    		 new Thread() {
    	            @Override
    	            public void run() {
    	                shellSocketServer.run();
    	            }
    	      }.start();
    	}else{
    		log.debug("shell socket has start");
    	}
    	
       
    }
}
