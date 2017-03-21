
package com.shinemo.publish.debug.event.methodentry;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.shinemo.publish.debug.jdivisitor.event.visitor.EmptyEventVisitor;
import com.sun.jdi.event.MethodEntryEvent;


/**
 * MethodEntryEvent 进入某个指定方法体时引发的事件
 * @author figo
 * 2017年1月16日
 */
public class MethodEntryVisitor extends EmptyEventVisitor {


	private final Map<String, Integer> methodCount;
	
    public MethodEntryVisitor() {
        methodCount = new HashMap<String, Integer>();
    }
    
    

    @Override
    public void visit(MethodEntryEvent event) {
        String className = event.method().declaringType().name();
        String method = className + "." + event.method().name();
        int count;

        if (!methodCount.containsKey(method)) {
            count = 1;
        } else {
            count = methodCount.get(method) + 1;
        }
        methodCount.put(method, count);
        
        printResults(methodCount);
    }

    public Map<String, Integer> getMethodCounts() {
    	MethodCountComparator comparator = new MethodCountComparator(
                methodCount);
        Map<String, Integer> sortedMethodCount = new TreeMap<String, Integer>(
                comparator);

        sortedMethodCount.putAll(methodCount);

        return sortedMethodCount;
    }
    
    
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


    private static class MethodCountComparator implements Comparator<String> {

        private final Map<String, Integer> map;

        public MethodCountComparator(Map<String, Integer> map) {
            this.map = map;
        }

        @Override
        public int compare(String o1, String o2) {
            if (map.get(o1) >= map.get(o2)) {
                return -1;
            } else {
                return 1;
            }
        }

    }
}
