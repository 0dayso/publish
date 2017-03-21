package com.shinemo.publish.common;


import com.shinemo.publish.api.BaseApi;

public class BaseResource extends BaseApi{
	
	public <T> ResultVO<T>  buildResultVO(Result<T> ret){
		if(ret.isSuccess()){
			return ResultVO.<T>success(ret.getValue());
		}else{
			return ResultVO.<T>fall(ret.getStatus(), ret.getMsg());
		}
	}
	
}
