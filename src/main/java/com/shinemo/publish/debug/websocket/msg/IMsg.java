package com.shinemo.publish.debug.websocket.msg;

public class IMsg {
	
	private String className;
	
	private String host;
	
	private int port;
	
	private int type;
	
	private String sid;
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isHeartBeat() {
        return 0 == type;
    }
	
	
	

}
