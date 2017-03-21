package com.shinemo.publish.common;


public class ResultVO<T> extends BaseDO{
	
	public static ResultVO<String> SUCCESS = success("success");
	
	private static final long serialVersionUID = 402699724877184241L;
	private boolean success;
	private String msg;
	private int code;
	private T data;
	
	private ResultVO(boolean success, String msg, int code,T data) {
		this.success = success;
		this.msg = msg;
		this.code = code;
		this.data = data;
	}
	
	public static <T> ResultVO<T> success(T data){
		ResultVO<T> vo = new ResultVO<T>(true,null,200,data);
		return vo;
	}
	
	public static <T> ResultVO<T> success(T data,String msg){
		ResultVO<T> vo = new ResultVO<T>(true,msg,200,data);
		return vo;
	}
	
	public static <T> ResultVO<T> success(T data,int code){
		ResultVO<T> vo = new ResultVO<T>(true,null,code,data);
		return vo;
	}
	
	public static <T> ResultVO<T> fall(Errors error){
		ResultVO<T> vo = new ResultVO<T>(false,error.getMsg(),error.getCode(),null);
		return vo;
	}
	
	public static <T> ResultVO<T> fall(int code,String msg){
		ResultVO<T> vo = new ResultVO<T>(false,msg,code,null);
		return vo;
	}
	
	public static <T> ResultVO<T> fall(String msg){
		ResultVO<T> vo = new ResultVO<T>(false,msg,200,null);
		return vo;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public  ResultVO<T> withCode(int code) {
		this.code = code;
		return this;
	}

}
