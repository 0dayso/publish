package com.shinemo.publish.debug.vm;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.shinemo.publish.debug.websocket.msg.StepMsg;
import com.sun.jdi.VirtualMachine;

public class VMAttr {
	
	private BlockingQueue<StepMsg> queue;
	
	private VirtualMachine vm;
	
	private Long uid;
	
	private String sid;
	
	private Date lastTime;
	
	private String host;
	
	private Integer port;
	
	
	public String getSid() {
		return sid;
	}


	public void setSid(String sid) {
		this.sid = sid;
	}


	public BlockingQueue<StepMsg> getQueue() {
		return queue;
	}


	public void setQueue(BlockingQueue<StepMsg> queue) {
		this.queue = queue;
	}


	public VMAttr(){
		
	}
	
	
	public VMAttr(VirtualMachine vm,Long uid,Date lastTime,String host,Integer port,String sid){
		this.vm = vm;
		this.uid = uid;
		this.lastTime = lastTime;
		this.host = host;
		this.port = port;
		this.sid = sid;
		this.queue = new ArrayBlockingQueue<StepMsg>(5);
	}

	public boolean valid(){
		if(vm ==null || host == null || uid == null || port == null || sid == null ){
			return false;
		}
		
		return true;
	}


	public VirtualMachine getVm() {
		return vm;
	}


	public void setVm(VirtualMachine vm) {
		this.vm = vm;
	}


	public Long getUid() {
		return uid;
	}


	public void setUid(Long uid) {
		this.uid = uid;
	}


	public Date getLastTime() {
		return lastTime;
	}


	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public Integer getPort() {
		return port;
	}


	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String toString(){
		return "uid:"+uid+";host:"+host+";port:"+port+";lastTime:"+lastTime+";sid:"+sid;
	}

}
