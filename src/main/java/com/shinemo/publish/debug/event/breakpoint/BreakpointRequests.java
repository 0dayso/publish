package com.shinemo.publish.debug.event.breakpoint;

import java.util.List;

import com.shinemo.publish.debug.BaseRequests;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;

public class BreakpointRequests extends BaseRequests {

	private String className;

	private int line;

	public BreakpointRequests(String className, int line) {
		this.className = className;
		this.line = line;
	}

	@Override
	public void requestEvents(EventRequestManager erm) {

		List<ReferenceType> types = erm.virtualMachine().classesByName(
				className);
		if (types == null || types.size() == 0) {
			System.out.println("No class found");
			return;
		}

		List<Location> locations;
		try {
			locations = types.get(0).locationsOfLine(line);
		} catch (AbsentInformationException e) {
			e.printStackTrace();
			return;
		}

		BreakpointRequest br = erm.createBreakpointRequest(locations.get(0));

		br.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);

		if (getCountFilter() != null) {
			Integer count = getCountFilter();
			if (count > 0) {
				br.addCountFilter(count);
			}
		}
		
		if(getSid() !=null){
			br.putProperty("sid", getSid());
		}
		br.putProperty("className", className);
		br.putProperty("line", line);

		br.enable();

	}

	//

}
