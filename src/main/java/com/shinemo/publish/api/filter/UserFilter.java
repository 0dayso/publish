package com.shinemo.publish.api.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.shinemo.power.client.common.UserContext;
import com.shinemo.power.client.common.UserContextHolder;
import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.constants.ProjectConstants;
import com.shinemo.publish.service.UsersService;
import com.shinemo.publish.utils.UserUtil;

public class UserFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(UserFilter.class);
	
	private UsersService usersService;
	
	private ApplicationContext context = null;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		// TODO Auto-generated method stub
		context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		usersService = (UsersService)context.getBean("usersService");

	}
	


	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) srequest;
		HttpServletResponse response = (HttpServletResponse) sresponse;
		
		StatusResponse statusResponse = new StatusResponse((HttpServletResponse)response); 
		chain.doFilter(request, statusResponse);
		
		
	}

	private String parseHttpRequest(HttpServletRequest request, long during) {
		StringBuilder sb = new StringBuilder();
		sb.append("TIME:").append((during / (1000 * 1000))).append("ms");
		sb.append("; URI:").append(request.getRequestURI());
		sb.append("; IP:").append(getRemortIP(request));
		sb.append("; METHOD:").append(request.getMethod());
		sb.append("; UA:").append(request.getHeader("User-Agent"));
		sb.append("; ").append("PARAMS:");
		
		Map<String, String[]> map = request.getParameterMap();
		for (String name : map.keySet()) {
			String[] values = map.get(name);
			sb.append("[").append(name).append("=").append(Arrays.toString(values));
		}
		return sb.toString();
	}

	private String getRemortIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");   
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
	        ip = request.getHeader("Proxy-Client-IP");   
	    }   
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
	        ip = request.getHeader("WL-Proxy-Client-IP");   
	  
	    }   
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
	        ip = request.getRemoteAddr();   
	    }   
	    return ip; 
	}


	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
