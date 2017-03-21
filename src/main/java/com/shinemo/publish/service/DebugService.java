package com.shinemo.publish.service;

import io.netty.channel.Channel;

import com.shinemo.publish.debug.websocket.msg.BreakPointMsg;
import com.shinemo.publish.debug.websocket.msg.FieldMsg;
import com.shinemo.publish.debug.websocket.msg.IMsg;
import com.shinemo.publish.debug.websocket.msg.StatMsg;
import com.shinemo.publish.debug.websocket.msg.StepMsg;

public interface DebugService {


	
	public void execBreakpointSock(Channel channel, BreakPointMsg msg) ;
	
	public void execAccesswatchSock(Channel channel, FieldMsg msg) ;
	
	public void execStepSock(Channel channel, StepMsg msg);
	
	public void execDisposeVm(Channel channel,IMsg msg);
	
	public void execStatVm(Channel channel,StatMsg msg);
	
	
	
	
}
