package com.shinemo.publish.debug.vm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.debug.websocket.msg.StepMsg;
import com.sun.jdi.VirtualMachine;

public class VMQueue {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(VMQueue.class);
	
	
	// not synchronized
	public static Map<String,VMAttr> VMMAP = new HashMap<String,VMAttr>();
	
	/**
	 * 新建debug成功后放入map
	 * @param vmAttr
	 * @return
	 */
	public static int put(VMAttr vmAttr){
		if(vmAttr!=null){
			if(!vmAttr.valid()) {
				LOG.error("put: vmAttr not valid!");
				return -1;
			}
			VMAttr va = VMMAP.get(vmAttr.getSid());
			if(va!=null){
				LOG.error("put: vmAttr exist!");
				return -2;
			}
			
			vmAttr.setLastTime(new Date());
			VMMAP.put(vmAttr.getSid(), vmAttr);
		}
		return 1;
	}
	
	/**
	 * 
	 * @param key
	 * @param uid
	 * @return
	 */
	public static VirtualMachine getVm(String key,Long uid){
		VMAttr va = VMMAP.get(key);
		LOG.info("VMMAP:"+VMMAP.toString());
		if(va !=null){
			if(va.getUid().longValue()==uid.longValue()){
				return va.getVm();
			}
		}
		return null;
	}
	
	
	
	/**
	 * 
	 * @param key
	 * @param uid
	 * @return
	 */
	public static BlockingQueue<StepMsg> getQueue(String key){
		VMAttr va = VMMAP.get(key);
		if(va !=null){
				return va.getQueue();
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param key
	 * @param uid
	 * @return
	 */
	public static VMAttr getVMAttr(String sid){
		return VMMAP.get(sid);
	}
	
	
	
	
	/**
	 * 
	 * @param vmAttr
	 * @return
	 */
	public static int update(VMAttr vmAttr,Long uid){
		if(vmAttr!=null){
			if(!vmAttr.valid()) {
				LOG.error("update: vmAttr not valid!");
				return -1;
			}
			VMAttr va = VMMAP.get(vmAttr.getSid());
			if(va!=null){
				if(va.getUid()==uid){
					vmAttr.setLastTime(new Date());
					VMMAP.put(vmAttr.getSid(), vmAttr);
					return 1;
				}
			}
			vmAttr.setLastTime(new Date());
			VMMAP.put(vmAttr.getSid(), vmAttr);
		}
		LOG.error("update: vmAttr not belong " + uid);
		return -2;
	}
	
	
	/**
	 * 释放VM
	 * @param key
	 * @param uid
	 */
	public static void release(String sid){
		VMAttr va = VMMAP.get(sid);
		if(va !=null){
			try {
				va.getVm().eventRequestManager().deleteAllBreakpoints();
				va.getVm().dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		VMMAP.remove(sid);
	}
	
	/**
	 * 释放VM
	 * @param key
	 * @param uid
	 */
	public static void release(String sid,long uid){
		VMAttr va = VMMAP.get(sid);
		if(va !=null){
			if(va.getUid()==uid){
				try {
					va.getVm().eventRequestManager().deleteAllBreakpoints();
					va.getVm().dispose();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		VMMAP.remove(sid);
	}
	
	
	public static void main(String[] args) {
		put(new VMAttr(null,106096l,new Date(),"127.0.0.1",8787,"sid"));
		VirtualMachine vm = getVm("127.0.0.1:8787", 106096l);
	}

}
