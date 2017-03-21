package com.shinemo.publish.debug.vm;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.api.util.SpringUtil;
import com.shinemo.publish.debug.jdivisitor.launcher.RemoteVMConnector;
import com.shinemo.publish.debug.jdivisitor.launcher.VMConnector;
import com.shinemo.publish.debug.websocket.msg.StepMsg;
import com.shinemo.publish.redis.service.RedisService;
import com.shinemo.publish.redis.util.RedisKey;
import com.sun.jdi.VirtualMachine;

public class VMTools {

	private static final Logger LOG = LoggerFactory.getLogger(VMTools.class);

	/**
	 * 链接debug端口
	 * 
	 * @return
	 */
	public static VirtualMachine connect(String host, int port) {
		try {
			VMConnector connector = new RemoteVMConnector(host, port);
			VirtualMachine vm = connector.connect();
			return vm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 链接debug端口
	 * 
	 * @return
	 */
	public static VirtualMachine connect(String host, int port, String sid) {
		try {
			long uid = getUid(sid);
			VirtualMachine vm = VMQueue.getVm(sid, uid);
			if (vm == null) {
				vm = connect(host, port);
				if (vm != null) {
					VMQueue.put(new VMAttr(vm, uid, new Date(), host, port,sid));
					return vm;
				}
			} else {
				VMQueue.update(new VMAttr(vm, uid, new Date(), host, port,sid), uid);
				return vm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		LOG.error("connect vm failed by sid:" + sid);
		return null;
	}

	public static BlockingQueue<StepMsg> getQueue(String sid) {
		return VMQueue.getQueue(sid);
	}

	public static long getUid(String sid) {
		RedisService redisService = (RedisService) SpringUtil
				.getBean("redisService");
		String namespace = RedisKey.getSessionKey(sid);
		String uid = redisService.hget(6, namespace, "userId", String.class);
		if (uid == null) {
			throw new RuntimeException("uid is null" + sid);
		}
		return Long.parseLong(uid);
	}

}
