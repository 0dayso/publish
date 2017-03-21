
package com.shinemo.publish.debug.event.accesswatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.debug.event.breakpoint.BreakpointVisitor;
import com.shinemo.publish.debug.jdivisitor.event.visitor.EmptyEventVisitor;
import com.shinemo.publish.debug.util.ValueUtil;
import com.shinemo.publish.debug.websocket.DebugSocketEcho;
import com.sun.jdi.Value;
import com.sun.jdi.event.AccessWatchpointEvent;
public class AccessWatchpointVisitor extends EmptyEventVisitor {

	private static final Logger logger = LoggerFactory
			.getLogger(BreakpointVisitor.class);

    @Override
    public void visit(AccessWatchpointEvent event) {
    	try {
    		event.thread().suspend();
    		logger.info("accesswatchpoint come in..");
    		String sid = getSid(event);
    		DebugSocketEcho.processText("ok",sid);
    		Value  o = event.object().getValue(event.field());
    		System.out.println(ValueUtil.parseValue(o));
    		DebugSocketEcho.processText(ValueUtil.parseValue(o),sid);
    		logger.info("accesswatchpoint go..");
    		
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	event.thread().resume();
    	//event.virtualMachine().dispose();
    }

}
