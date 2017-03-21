package com.shinemo.publish.service.impl;

import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.debug.event.accesswatch.AccessWatchpointRequests;
import com.shinemo.publish.debug.event.accesswatch.AccessWatchpointVisitor;
import com.shinemo.publish.debug.event.breakpoint.BreakpointRequests;
import com.shinemo.publish.debug.event.breakpoint.BreakpointVisitor;
import com.shinemo.publish.debug.jdivisitor.Debugger;
import com.shinemo.publish.debug.vm.VMAttr;
import com.shinemo.publish.debug.vm.VMQueue;
import com.shinemo.publish.debug.vm.VMTools;
import com.shinemo.publish.debug.websocket.DebugSocketEcho;
import com.shinemo.publish.debug.websocket.msg.BreakPointMsg;
import com.shinemo.publish.debug.websocket.msg.FieldMsg;
import com.shinemo.publish.debug.websocket.msg.IMsg;
import com.shinemo.publish.debug.websocket.msg.StatMsg;
import com.shinemo.publish.debug.websocket.msg.StepMsg;
import com.shinemo.publish.service.DebugService;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

@Service("debugService")
public class DebugServiceImpl implements DebugService {


	private Logger logger = LoggerFactory.getLogger(DebugServiceImpl.class);


	@Override
	public void execBreakpointSock(Channel channel, BreakPointMsg msg) {
		BreakpointRequests requests = new BreakpointRequests(msg.getClassName(),msg.getLine());
		requests.withSid(msg.getSid());
		requests.withCountFilter(1);
		BreakpointVisitor visitor = new BreakpointVisitor();
		VirtualMachine vm = VMTools.connect(msg.getHost(), msg.getPort(),msg.getSid());
		Debugger debugger = new Debugger(vm);
		debugger.requestEvents(requests);
		debugger.run(visitor,  300000);
	}
	
	
	
	@Override
	public void execAccesswatchSock(Channel channel, FieldMsg msg) {
		AccessWatchpointRequests requests = new AccessWatchpointRequests(msg.getClassName(),msg.getField());
		requests.withSid(msg.getSid());
		requests.withCountFilter(1);
		AccessWatchpointVisitor visitor = new AccessWatchpointVisitor();
		VirtualMachine vm = VMTools.connect(msg.getHost(), msg.getPort(),msg.getSid());
		Debugger debugger = new Debugger(vm);
		debugger.requestEvents(requests);
		debugger.run(visitor,  3000);
	}



	@Override
	public void execStepSock(Channel channel, StepMsg msg) {
		BlockingQueue<StepMsg> queue = VMTools.getQueue(msg.getSid());
		if(queue==null){
			logger.error("queue is null");
			return ;
		}try {
			queue.put(msg);
		} catch (Exception e) {
			logger.error("put step failed:",e);
		}
		
	}



	@Override
	public void execDisposeVm(Channel channel, IMsg msg) {
		VirtualMachine vm = VMTools.connect(msg.getHost(), msg.getPort(), msg.getSid());
		logger.error(vm.description()+" VM release..");
		VMQueue.release(msg.getSid());
		DebugSocketEcho.processText("当前debug链接已断开", msg.getSid());
		DebugSocketEcho.processResult(ResultVO.success("vm dispose", DebugSocketEcho.RELEASE_CODE), msg.getSid());
	}



	@Override
	public void execStatVm(Channel channel, StatMsg msg) {
		VMAttr vmAttr  = VMQueue.getVMAttr(msg.getSid());
		VirtualMachine vm = vmAttr.getVm();
		DebugSocketEcho.processText(vm.toString(), msg.getSid());
		for ( ThreadReference thread : vm.allThreads() ){
    		String status = "";
    		switch ( thread.status() ) {
    			case ThreadReference.THREAD_STATUS_MONITOR:
    				status = "monitor"; break;
    			case ThreadReference.THREAD_STATUS_NOT_STARTED:
    				status = "not started"; break;
    			case ThreadReference.THREAD_STATUS_RUNNING:
    				status = "running"; break;
    			case ThreadReference.THREAD_STATUS_SLEEPING:
    				status = "sleep"; break;
    			case ThreadReference.THREAD_STATUS_UNKNOWN:
    				status = "unknown"; break;
    			case ThreadReference.THREAD_STATUS_WAIT:
    				status = "wait"; break;
    			case ThreadReference.THREAD_STATUS_ZOMBIE:
    				status = "zombie"; break;
    		}
    		DebugSocketEcho.processText( thread.name() + "' - " + status + (thread.isSuspended()?" (suspended)":"") , msg.getSid());
    	}
	}



}
