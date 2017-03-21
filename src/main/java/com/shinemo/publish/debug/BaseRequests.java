package com.shinemo.publish.debug;

import com.shinemo.publish.debug.jdivisitor.request.EventRequestor;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;


public class BaseRequests implements EventRequestor {


	protected ClassFilter classFilter;
	
	protected ClassExclusionFilter classExclusionFilter;
	
	protected Integer count;
	
	protected String sid;
	
	

    public String getSid() {
		return sid;
	}

	public BaseRequests withSid(String sid) {
		this.sid = sid;
		return this;
	}

	public BaseRequests withClassFilter(ClassFilter classFilter){
    	this.classFilter = classFilter;
    	return this;
    }
    
    public BaseRequests withClassExclusionFilter(ClassExclusionFilter classExclusionFilter){
    	this.classExclusionFilter = classExclusionFilter;
    	return this;
    }
    
    public BaseRequests withCountFilter(Integer count){
    	this.count = count;
    	return this;
    }

	public ClassFilter getClassFilter() {
		return classFilter;
	}

	public ClassExclusionFilter getClassExclusionFilter() {
		return classExclusionFilter;
	}
    
	public Integer getCountFilter() {
		return count;
	}
	
	protected void processRequest(EventRequest  erm){
		
		
	}
    

    @Override
	public void requestEvents(EventRequestManager erm) {
    	
    }
    
    //
    
}
