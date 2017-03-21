package com.shinemo.publish.utils;

import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockUtils {

	private static final Logger LOG = LoggerFactory
			.getLogger(LockUtils.class);
	
	private static ReentrantLock lock = new ReentrantLock();

	public static boolean get(long times) {
		try {
			if (lock.tryLock()){
				try {
					Thread.sleep(times);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
			return false;
		} finally {
			try {
				lock.unlock();
			} catch (Exception e) {
			}
			
		}
	}
	
	public static boolean get() {
		return get(2000);
	}

}
