package com.shinemo.publish.websocket;



public class ShellSocketMsg {
    // 0:心跳 1:shell cmd
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
