
package com.shinemo.publish.debug.event.methodentry;

import java.util.Map;

import com.shinemo.publish.debug.ClassFilter;
import com.shinemo.publish.debug.jdivisitor.Debugger;
import com.shinemo.publish.debug.jdivisitor.launcher.RemoteVMConnector;
import com.shinemo.publish.debug.jdivisitor.launcher.VMConnector;
import com.sun.jdi.VirtualMachine;

public final class MethodEntryTest {

	static String MAIN = "org.jdivisitor.examples.HelloWorld1";
	static String OPTIONS = "-cp /Users/luohuajun/work/workspace/git/debug/debug/target/classes";

	public static void main(String[] args) throws Exception {
//		if (args.length < 1) {
//			System.err.println("Incorrect number of command-line arguments");
//			System.err.println("Usage: MethodProfiler <main-class> <options>");
//
//			System.exit(1);
//		}
		

		MethodEntryRequests requests = new MethodEntryRequests();
		requests.withClassFilter(new ClassFilter("com.shinemo.*"));
		MethodEntryVisitor visitor = new MethodEntryVisitor();

		//VMConnector connector = new LocalVMLauncher(MAIN, OPTIONS, null,null);
		VMConnector connector = new RemoteVMConnector("127.0.0.1", 12345);

		VirtualMachine vm = connector.connect();

		Debugger debugger = new Debugger(vm);
		debugger.requestEvents(requests);
		debugger.run(visitor,  1000); // Maximum runtime of 10 seconds

	}

	/**
	 * Pretty print the results.
	 *
	 * @param methodTable
	 *            Table containing the results
	 */
	private static void printResults(Map<String, Integer> methodTable) {
		// Print the header
		System.out.format("%-70s %5s\n", "Method", "Count");
		for (int i = 0; i < (70 + 1 + 5); i++) {
			System.out.print("=");
		}
		System.out.println();

		// Print the results
		for (Map.Entry<String, Integer> entry : methodTable.entrySet()) {
			String method = entry.getKey();
			int count = entry.getValue();

			System.out.format("%-70s %5d\n", method, count);
		}
	}
}
