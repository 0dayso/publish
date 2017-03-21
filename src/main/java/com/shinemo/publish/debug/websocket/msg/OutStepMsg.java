package com.shinemo.publish.debug.websocket.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单步调试输出
 * @author figo
 * 2017年3月17日
 */
public class OutStepMsg {
	
	/**
	 * 全局变量
	 */
	private List<Map<String,String>> fields;
	
	/**
	 * 局部变量
	 */
	private List<Map<String,String>> locals;
	
	/**
	 * 当前行
	 */
	private int line;

	public List<Map<String, String>> getFields() {
		return fields;
	}

	public void setFields(List<Map<String, String>> fields) {
		this.fields = fields;
	}

	public List<Map<String, String>> getLocals() {
		return locals;
	}

	public void setLocals(List<Map<String, String>> locals) {
		this.locals = locals;
	}

	public int getLine() {
		return line;
	}

	public OutStepMsg setLine(int line) {
		this.line = line;
		return this;
	}
	
	public OutStepMsg addField(String name,String value){
		if(fields==null){
			fields = new ArrayList<Map<String,String>>();
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put(name, value);
		fields.add(map);
		return this;
	}
	
	public OutStepMsg addLocal(String name,String value){
		if(locals==null){
			locals = new ArrayList<Map<String,String>>();
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put(name, value);
		locals.add(map);
		return this;
	}
	

}
