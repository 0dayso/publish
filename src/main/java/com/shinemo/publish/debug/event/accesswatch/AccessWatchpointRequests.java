package com.shinemo.publish.debug.event.accesswatch;

import java.util.List;

import com.shinemo.publish.debug.BaseRequests;
import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;


public class AccessWatchpointRequests extends BaseRequests {
	
	private String className;
	
	private String fieldName;
	
	public AccessWatchpointRequests(String className,String fieldName){
		this.className = className;
		this.fieldName = fieldName;
	}

    @Override
	public void requestEvents(EventRequestManager erm) {
    	
    	List<ReferenceType> types = erm.virtualMachine().classesByName(className);
    	if (types == null || types.size() == 0) {  
            System.out.println("No class found");  
            return;  
        }  
    	
        ReferenceType rt = types.get(0);
        Field field = rt.fieldByName(fieldName);
    	rt.fields();
    	
    	
    	AccessWatchpointRequest awp = erm.createAccessWatchpointRequest(field);

    	awp.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        
        if(getClassFilter() != null){
        	for (String clazzFilter : getClassFilter().getClassFilters()) {
        		awp.addClassFilter(clazzFilter);
			}
        }
        
        if(getClassExclusionFilter() != null){
        	for (String clazzExclusionFilter : getClassExclusionFilter().getClassExclusionFilters()) {
        		awp.addClassExclusionFilter(clazzExclusionFilter);
			}
        }
        
        if(getCountFilter() != null){
        	Integer count = getCountFilter();
        	if(count>0){
        		awp.addCountFilter(count);
        	}
        }
        
        awp.enable();
        
    }
    
    //
    
}
