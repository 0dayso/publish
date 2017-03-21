package com.shinemo.publish.api;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.shinemo.publish.api.base.ExceptionConstants;
import com.shinemo.publish.api.util.JacksonUtil;
import com.shinemo.publish.common.SimpleResult;

/**
 * Created by david on 15/9/9.
 */
public abstract class BaseApi {
	
	
	/**
	 * 转化object to string
	 * 
	 * @param resps
	 * @return
	 * @throws Exception
	 */
	protected static Response getObjectResponse(SimpleResult resps) throws Exception{
		if(resps.isSuccess()){
			String r = JacksonUtil.convertFrom(resps.getData());
            return Response.status(resps.getStatus()).entity(r).build();
        }else{
        	return Response.status(resps.getStatus()).entity(ExceptionConstants.buildStatusException(resps)).build();
        }
	}
	
	
	protected static Response getResponse(SimpleResult resps) throws Exception{
		if(resps.isSuccess()){
            return Response.status(resps.getStatus()).build();
        }else{
        	if(resps.getMessage().indexOf("message")!=-1){
        		return Response.status(resps.getStatus()).entity(resps.getMessage()).build();
        	}else{
        		return Response.status(resps.getStatus()).entity(ExceptionConstants.buildStatusException(resps)).build();
        	}
        }
	}
	
	protected static Response getByteResponse(SimpleResult<byte[]> resps)throws Exception{
		if(resps.isSuccess()){
            return Response.status(resps.getStatus()).entity(resps.getData()).build();
        }else{
        	if(resps.getMessage().indexOf("message")!=-1){
        		return Response.status(resps.getStatus()).entity(resps.getMessage()).build();
        	}else{
        		return Response.status(resps.getStatus()).entity(ExceptionConstants.buildStatusException(resps)).build();
        	}
        }
	}
	
	protected static Integer asInteger(String value){
		if(StringUtils.isBlank(value)){
			return null;
		}
		if(!NumberUtils.isNumber(value)){
			return null;
		}
		return Integer.valueOf(value);
	}
	
	
}
