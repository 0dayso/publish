package com.shinemo.publish.debug.websocket.msg;


public enum MsgEnum {
	
	PING(0,"心跳"), 
	BREAKEPOINT(1,"com.shinemo.publish.debug.websocket.msg.BreakPointMsg"),
	STEP(2,"com.shinemo.publish.debug.websocket.msg.StepMsg"),
	FIELD(3,"com.shinemo.publish.debug.websocket.msg.FieldMsg"),
	LOCALVARIABLE(4,"com.shinemo.publish.debug.websocket.msg.LocalVariableMsg"),
	RESUME(5,"com.shinemo.publish.debug.websocket.msg.ResumeMsg"),
	DISCONNECT(6,"com.shinemo.publish.debug.websocket.msg.DisConnectMsg"),
	STAT(7,"com.shinemo.publish.debug.websocket.msg.StatMsg"),
	OTHER(9999,"其他");
	
    private Integer type;
    private String clazz;
    
    private MsgEnum(Integer type, String clazz) {
        this.type = type;
        this.clazz = clazz;
    }
	
    public static MsgEnum getByType(Integer type) {
    	if(type==null) return OTHER; 
        for (MsgEnum msg : values()) {
            if (msg.getType().equals(type)) {
                return msg;
            }
        }
        return OTHER;
    }

	public Integer getType() {
		return type;
	}

	public String getClazz() {
		return clazz;
	}

    

}
