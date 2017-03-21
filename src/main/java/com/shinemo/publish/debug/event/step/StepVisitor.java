
package com.shinemo.publish.debug.event.step;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.debug.jdivisitor.Debugger;
import com.shinemo.publish.debug.jdivisitor.event.visitor.EmptyEventVisitor;
import com.shinemo.publish.debug.util.DebugConstants;
import com.shinemo.publish.debug.util.ValueUtil;
import com.shinemo.publish.debug.vm.VMTools;
import com.shinemo.publish.debug.websocket.DebugSocketEcho;
import com.shinemo.publish.debug.websocket.msg.OutStepMsg;
import com.shinemo.publish.debug.websocket.msg.StepMsg;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.StepRequest;
public class StepVisitor extends EmptyEventVisitor {

	private static final Logger logger = LoggerFactory
			.getLogger(StepVisitor.class);
	

    @Override
    public void visit(StepEvent event) {
    	try {
    		
    		String sid = getSid(event);
    		/////
    		
    		if(processStep(sid, event)){
    			nextStep(event, sid);
    		}
    		
    		
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	
    }


	private void nextStep(StepEvent event, String sid) {
		logger.info("next step.");
		String className = getClassName(event);
		Integer line = getLine(event);
		StepRequests stepRequests= new StepRequests(event.thread(),className,line);
		stepRequests.withSid(sid).withCountFilter(1);
		StepVisitor visitor = new StepVisitor();
		VirtualMachine vm = event.virtualMachine();
		Debugger debugger = new Debugger(vm);
		debugger.requestEvents(stepRequests);
		debugger.run(visitor,  1);
	}
    
    
    private boolean processStep(String sid,StepEvent event) {
    	event.thread().suspend();
    	boolean debugContinue = true;
		final ExecutorService service = Executors.newFixedThreadPool(1);
		TaskThread taskThread = new TaskThread(sid);
		Future<Object> taskFuture = null;
		OutStepMsg msg = new OutStepMsg();
		while(true){
			try {
				taskFuture = service.submit(taskThread);
				Object re = taskFuture.get(DebugConstants.STEP_TIMEOUT, TimeUnit.MILLISECONDS);// 超时设置
				if(re!=null){
					logger.info("debug step event ...begin ...");
					StepMsg smsg = (StepMsg) re;
					if(smsg !=null && smsg.getStepType() == StepRequest.STEP_OUT){		//跳出debug
						logger.info("debug step over!");
						event.request().disable();
						DebugSocketEcho.processText("["+event.thread().name()+"] step over", sid);
						ResultVO<String> result =  ResultVO.success("step over", DebugSocketEcho.STEPOVER_CODE);
			    		DebugSocketEcho.processResult(result, sid);
			    		debugContinue = false;
			    		break;
					}
					StackFrame stackFrame = event.thread().frame(0); 
					msg.setLine(event.location().lineNumber());
		    		if(event.location().lineNumber()>=getLine(event)){	//开始断点 step by step
			    		DebugSocketEcho.processText("["+event.thread().name()+"] current Line:"+event.location().lineNumber(),sid);
		        		List<LocalVariable> localVariables = stackFrame.visibleVariables();
		        		for (LocalVariable localVariable : localVariables) {
		        			Value v = stackFrame.getValue(localVariable);
		        			msg.addLocal(localVariable.name(), ValueUtil.parseValue(v));
		    			}
		    		}
		    		List<Field> fields = stackFrame.thisObject().referenceType().allFields();
		    		for (Field field : fields) {
		    			Value value = stackFrame.thisObject().getValue(field);
		    			msg.addField(field.name(), ValueUtil.parseValue(value));
		            }
		    		
		    		ResultVO<OutStepMsg> result =  ResultVO.success(msg).withCode(DebugSocketEcho.OK_CODE);
		    		DebugSocketEcho.processResult(result, sid);
		    		
		    		if(smsg !=null && smsg.getStepType() == StepRequest.STEP_INTO){
		    			logger.info("debug into step !");
		    		}
		    		
		    		if(smsg !=null && smsg.getStepType() == StepRequest.STEP_OVER){		//next step
						logger.info("debug next step !");
			    		debugContinue = true;
			    		break;
					}
				}
				Thread.sleep(500);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				logger.error("debug step timeout ...");
				taskFuture.cancel(true);
				DebugSocketEcho.processResult(ResultVO.success("timeout & cancel", DebugSocketEcho.STEPOVER_CODE),sid);
				DebugSocketEcho.processText("["+event.thread().name()+"] Debug setp event timeout & cancel !",sid);
				event.request().disable();
				service.shutdown();
				debugContinue = false;
				break;
			} catch(Exception e){
				
			}finally {
			}
		}
		
		event.thread().resume();
    	//执行一次完就删除
    	event.virtualMachine().eventRequestManager().deleteEventRequest(event.request());
    	return debugContinue;
	}
    

}

class TaskThread implements Callable<Object> {
	
	private String sid;
	
	TaskThread(String sid){
		this.sid = sid;
	}

	public Object call() throws Exception {
		BlockingQueue<StepMsg> queue = VMTools.getQueue(sid);
		return queue.take();
	}

}
