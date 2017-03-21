
package com.shinemo.publish.debug.event.breakpoint;

import com.shinemo.publish.debug.jdivisitor.Debugger;
import com.shinemo.publish.debug.jdivisitor.launcher.RemoteVMConnector;
import com.shinemo.publish.debug.jdivisitor.launcher.VMConnector;
import com.shinemo.publish.debug.vm.VMTools;
import com.sun.jdi.VirtualMachine;

public final class BreakpointTest {

	static String MAIN = "com.shinemo.debug.test.HelloWorld";
	static String OPTIONS = "-cp /Users/luohuajun/work/workspace/git/debug/debug/target/classes";

	public static void main(String[] args) throws Exception {

		BreakpointRequests requests = new BreakpointRequests(MAIN,32);
		requests.withCountFilter(1);
		BreakpointVisitor visitor = new BreakpointVisitor();
		
		VirtualMachine vm = VMTools.connect("127.0.0.1", 12345);

		Debugger debugger = new Debugger(vm);
		debugger.requestEvents(requests);
		debugger.run(visitor,  3000); // Maximum runtime of 10 seconds

		//printResults(visitor.getMethodCounts());
	}

}
