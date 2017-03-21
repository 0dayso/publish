package com.shinemo.publish.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.shinemo.publish.utils.ServerConfig;

/**
 * 服务器列表
 * @author figo
 * 2016年6月12日
 */
public class ServerConstants {

	private static Map<String,String> servers = new LinkedHashMap<String,String>();
	
	static{
		
		String s = ServerConfig.get("server.list");
		if(s!=null){
			try {
				String[] serverList = StringUtils.split(s, ",");
				for (String serverArr : serverList) {
					String[] server = StringUtils.split(serverArr, "|");
					servers.put(server[0],server[1]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		//彩云java
//		servers.put("shinemo-safe:10.132.21.123:9092", "预发机器[10.132.21.123]");
//		servers.put("shinemo-safe:10.51.27.114:9092", "彩云webNode1[10.51.27.114]");
//		servers.put("shinemo-safe:10.117.15.102:9092", "彩云webNode2[10.117.15.102]");
//		servers.put("shinemo-safe:10.161.237.190:9092", "彩云webNode3[10.161.237.190]");
//		servers.put("shinemo-safe:10.168.155.202:9092", "彩云webNode4[10.168.155.202]");
//		servers.put("shinemo-safe:10.117.60.94:9092", "彩云webNode5[10.117.60.94]");
//		servers.put("shinemo-safe:10.168.76.126:9092", "彩云apiNode1[10.168.76.126]");
//		servers.put("shinemo-safe:10.117.15.99:9092", "彩云apiNode2[10.117.15.99]");
//		
//		servers.put("shinemo-safe:10.117.70.72:9092", "麻绳javaNode[10.117.70.72]");
		
		
		
//		servers.put("shinemo-safe:10.95.221.133:22", "[10.95.221.133]");
//		servers.put("shinemo-safe:10.95.221.134:22", "[10.95.221.133]");
//		servers.put("shinemo-safe:10.95.221.135:22", "[10.95.221.133]");
//		servers.put("shinemo-safe:10.95.221.136:22", "[10.95.221.133]");
		//麻绳java
		
		//静态
		
	}
	
	
	public static Map<String, String> sortMapByValue() {  
	    Map<String, String> sortedMap = new LinkedHashMap<String, String>();  
	    if (servers != null && !servers.isEmpty()) {  
	        List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(servers.entrySet());  
	        Collections.sort(entryList,  
	                new Comparator<Map.Entry<String, String>>() {  
	                    public int compare(Entry<String, String> entry1,  
	                            Entry<String, String> entry2) {  
	                        int value1 = 0, value2 = 0;  
	                        try {  
	                            value1 = getInt(entry1.getValue());  
	                            value2 = getInt(entry2.getValue());  
	                        } catch (NumberFormatException e) {  
	                            value1 = 0;  
	                            value2 = 0;  
	                        }  
	                        return value2 - value1;  
	                    }  
	                });  
	        Iterator<Map.Entry<String, String>> iter = entryList.iterator();  
	        Map.Entry<String, String> tmpEntry = null;  
	        while (iter.hasNext()) {  
	            tmpEntry = iter.next();  
	            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());  
	        }  
	    }  
	    return sortedMap;  
	} 
	
	
	private static int getInt(String str) {  
	    int i = 0;  
	    try {  
	        Pattern p = Pattern.compile("^\\d+");  
	        Matcher m = p.matcher(str);  
	        if (m.find()) {  
	            i = Integer.valueOf(m.group());  
	        }  
	    } catch (NumberFormatException e) {  
	        e.printStackTrace();  
	    }  
	    return i;  
	}
	

}
