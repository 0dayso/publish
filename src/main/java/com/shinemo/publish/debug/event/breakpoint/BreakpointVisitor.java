
package com.shinemo.publish.debug.event.breakpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.debug.event.step.StepRequests;
import com.shinemo.publish.debug.event.step.StepVisitor;
import com.shinemo.publish.debug.jdivisitor.Debugger;
import com.shinemo.publish.debug.jdivisitor.event.visitor.EmptyEventVisitor;
import com.shinemo.publish.debug.websocket.DebugSocketEcho;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
public class BreakpointVisitor extends EmptyEventVisitor {

	private static final Logger logger = LoggerFactory
			.getLogger(BreakpointVisitor.class);

    @Override
    public void visit(BreakpointEvent event) {
    	try {
    		event.thread().suspend();
    		logger.error("breakpoint come in..");
    		//StackFrame stackFrame = event.thread().frame(0); 
    		//stackFrame.
    		String sid = getSid(event);
    		
    		DebugSocketEcho.processText("start step debug ",sid);
    		
    		/****** run steprequest  ****/
    		String className = getClassName(event);
    		Integer line = getLine(event);
    		StepRequests stepRequests= new StepRequests(event.thread(),className,line);
    		stepRequests.withSid(sid);
    		stepRequests.withCountFilter(1);
    		StepVisitor visitor = new StepVisitor();
    		VirtualMachine vm = event.virtualMachine();
    		Debugger debugger = new Debugger(vm);
    		debugger.requestEvents(stepRequests);
    		debugger.run(visitor,  2000);
    		logger.error("run a stepEvent thread:" + event.thread().name());
    		/******   ****/
    		
    		
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	event.thread().resume();
    	
    	//执行一次完就删除
    	event.virtualMachine().eventRequestManager().deleteEventRequest(event.request());
    }

}
