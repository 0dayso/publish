package com.shinemo.publish.debug.websocket;



public class DebugSocketMsg {
    // 0:心跳 1:stepOver 2:Resume 3:Breakpoint 4:Field 5:Invoke 
    private byte type;
    // shell
    private String shell;
    
    private String hostinfo;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    
    public String getShell() {
		return shell;
	}

	public void setShell(String shell) {
		this.shell = shell;
	}

	public boolean isHeartBeat() {
        return 0 == type;
    }

	public String getHostinfo() {
		return hostinfo;
	}

	public void setHostinfo(String hostinfo) {
		this.hostinfo = hostinfo;
	}
	
}
