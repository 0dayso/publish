/**
 * 
 */
package com.shinemo.publish.api.base;

import net.sf.json.JSONObject;

import com.shinemo.publish.common.SimpleResult;

/**
 * @author david
 *
 */
public class ExceptionConstants {

	/**
	 * api server error pair
	 */
	public static final Integer API_SERVER_ERROR_CODE = 500;
	
	public static final String API_SERVER_ERROR_STRING = "api server error";
	
	public static String buildApiServiceException(){
		JSONObject o = new JSONObject();
		o.put("message", API_SERVER_ERROR_STRING);
		return o.toString();
	}
	
	public static String buildStatusException(SimpleResult resp){
		JSONObject o = new JSONObject();
		o.put("message", resp.getMessage());
		return o.toString();
	}
	
	/**
	 * medical server error pair
	 */
	public static final Integer SERVER_ERROR_CODE = 700;
	
	public static final String SERVER_ERROR_STRING = "medical server error";
	
	/**
	 * param erroo pair
	 */
	public static final Integer INPUT_PARAM_ERROR_CODE = 701;
	
	public static final String INPUT_PARAM_ERROR_STRING = "input param error";
	
	/**
	 * ok pair
	 */
	public static final Integer OK_CODE = 200;
	
	public static final String OK_STRING = "ok";
}
