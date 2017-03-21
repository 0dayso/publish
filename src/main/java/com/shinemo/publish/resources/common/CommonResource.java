package com.shinemo.publish.resources.common;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.power.client.anno.IgnoreLogin;
import com.shinemo.publish.common.BaseResource;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.utils.UserUtil;

@RequestMapping("/")
@Controller
public class CommonResource extends BaseResource {
	
    @RequestMapping(value="/invalid.htm",method=RequestMethod.GET)
    @IgnoreLogin
    public String invalid(){
    	return "invalid";
    }
    
    @RequestMapping(value={"/index.htm","/index.html","/index"},method=RequestMethod.GET)
    public String index(){
    	return "index";
    }
    
    
    @RequestMapping(value={"/test.htm","/test.html","/test"},method=RequestMethod.GET)
    public String test(){
    	return "testshell";
    }
    
    @RequestMapping(value = "/checkstatus")
	@ResponseBody
	@IgnoreLogin
	public String checkstatus() {
		return "success";
    }
}
