package com.shinemo.publish.debug.websocket.msg;

import com.sun.jdi.request.StepRequest;

public class StepMsg extends IMsg {
	private static int STEP_INTO = StepRequest.STEP_INTO;
	private static int STEP_OVER = StepRequest.STEP_OVER;
	private static int STEP_OUT = StepRequest.STEP_OUT;
	
	private int stepType;
	
	private String thread;
	
	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	public int getStepType() {
		return stepType;
	}

	public void setStepType(int stepType) {
		this.stepType = stepType;
	}
	

}
