package com.shinemo.publish.debug.event.methodentry;

import com.shinemo.publish.debug.BaseRequests;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;


public class MethodEntryRequests extends BaseRequests {

    @Override
	public void requestEvents(EventRequestManager erm) {
    	
    	
        // Exclude some packages
        MethodEntryRequest mer = erm.createMethodEntryRequest();
        mer.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        
        if(getClassFilter() != null){
        	for (String clazzFilter : getClassFilter().getClassFilters()) {
        		mer.addClassFilter(clazzFilter);
			}
        }
        
        if(getClassExclusionFilter() != null){
        	for (String clazzExclusionFilter : getClassExclusionFilter().getClassExclusionFilters()) {
        		mer.addClassExclusionFilter(clazzExclusionFilter);
			}
        }
        
        mer.enable();
    }
    
    //
    
}
