package com.shinemo.publish.debug.event.step;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.debug.BaseRequests;
import com.shinemo.publish.debug.event.breakpoint.BreakpointVisitor;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

public class StepRequests extends BaseRequests {
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(StepRequests.class);

	private String className;

	private int line;
	
	private ThreadReference thread;

	public StepRequests(ThreadReference thread,String className, int line) {
		this.className = className;
		this.line = line;
		this.thread = thread;
	}

	@Override
	public void requestEvents(EventRequestManager erm) {

		List<ReferenceType> types = erm.virtualMachine().classesByName(
				className);
		if (types == null || types.size() == 0) {
			System.out.println("No class found");
			return;
		}

		StepRequest sr = erm.createStepRequest(thread, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
		logger.info("stepRequest classFilter:"+className);
		sr.addClassFilter(className);
		sr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);

		if (getCountFilter() != null) {
			Integer count = getCountFilter();
			if (count > 0) {
				sr.addCountFilter(count);
			}
		}
		
		if(getSid() !=null){
			sr.putProperty("sid", getSid());
		}
		sr.putProperty("className", className);
		sr.putProperty("line", line);

		sr.enable();

	}

	//

}
