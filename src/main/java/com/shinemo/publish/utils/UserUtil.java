package com.shinemo.publish.utils;

import javax.servlet.http.HttpServletRequest;

import com.shinemo.publish.client.Users;
import com.shinemo.publish.constants.ProjectConstants;

public class UserUtil {
	
	public static String user_attr = "user_attr";
	
	public static Users get(HttpServletRequest request){
		return (Users)request.getAttribute(user_attr);
	}
	
	public static void put(HttpServletRequest request,Users users){
		request.setAttribute(user_attr,users);
	}
	
	public static Long getUid(HttpServletRequest request){
		Users users = get(request);
		if(users!=null){
			return users.getId();
		}
		return 0l;
	}
	
	public static String getUserName(HttpServletRequest request){
		Users users = get(request);
		if(users!=null){
			return users.getName();
		}
		return "未知";
	}
	
	public static Long getAclUid(HttpServletRequest request){
		Users users = get(request);
		if(users!=null){
			return users.getSsoUserId();
		}
		return 0l;
	}
	
	
	
	public static boolean isSuperAdmin(HttpServletRequest request){
		Users users = get(request);
		if(ProjectConstants.USER_TYPE_SUPERADMIN == users.getType()){
			return true;
		}
		return false;
	}
	
	//包含超级管理员
	public static boolean isAdmin(HttpServletRequest request){
		Users users = get(request);
		if(ProjectConstants.USER_TYPE_SUPERADMIN == users.getType() || ProjectConstants.USER_TYPE_ADMIN == users.getType()){
			return true;
		}
		return false;
	}
	

}
