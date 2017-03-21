package com.shinemo.publish.resources.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.common.BaseResource;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.service.ProjectService;
import com.shinemo.publish.service.ShellService;
import com.shinemo.publish.utils.HttpUtils;

@RequestMapping("/monitor")
@Controller
public class MonitorResource extends BaseResource {

	@Resource
	private ProjectService projectService;
	
	@Resource
	private ShellService shellService;

	@RequestMapping(value = "/wars")
	@ResponseBody
	public ResultVO<List<Map<String,Object>>> getWars(@Context HttpServletRequest request) {
		ProjectQuery query = new ProjectQuery();
		query.setType(0);
		query.setOrderByType(ProjectQuery.OrderByTypeEnum.ENV_ASC);
		Result<List<Project>> ret = projectService.find(query);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(ret!=null && ret.isSuccess()){
			List<Project> projects = ret.getValue();
			for (Project project : projects) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", project.getId());
				map.put("name", project.getName());
				//环境
				if(project.getEnv()==-1) map.put("env", "讯盟");
				else if(project.getEnv()==0) map.put("env", "优办");
				else if(project.getEnv()==1) map.put("env", "彩云");
				//tomcat
				map.put("tomcat", getTomcat(project.getTargetPath()));
				map.put("host",processHost( project.getOnlineHost()));
				map.put("checkUri",project.getCheckUri());
				list.add(map);
			}
		}
		return ResultVO.success(list);
	}
	
	
	@RequestMapping(value = "/checkAllWars")
	@ResponseBody
	public ResultVO<List<Map<String,Object>>> checkAllWars(@Context HttpServletRequest request) {
		ProjectQuery query = new ProjectQuery();
		query.setType(0);
		query.setOrderByType(ProjectQuery.OrderByTypeEnum.ENV_ASC);
		Result<List<Project>> ret = projectService.find(query);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(ret!=null && ret.isSuccess()){
			List<Project> projects = ret.getValue();
			for (Project project : projects) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", project.getId());
				map.put("name", project.getName());
				//环境
				if(project.getEnv()==-1) map.put("env", "讯盟");
				else if(project.getEnv()==0) map.put("env", "优办");
				else if(project.getEnv()==1) map.put("env", "彩云");
				//tomcat
				map.put("tomcat", getTomcat(project.getTargetPath()));
				map.put("checkUri",project.getCheckUri());
				Map m = checkStatus(project.getCheckUri(), project.getTargetPath(), project.getOnlineHost(), project.getRemoteUser());
				map.put("host",m);
				list.add(map);
				
			}
		}
		return ResultVO.success(list);
	}
	
	
	/**
	 * 单个机器检测
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/checkStatus")
	@ResponseBody
	public ResultVO checkStatus(@Context HttpServletRequest request,@QueryParam("id") long id) {
		ProjectQuery query = new ProjectQuery();
		query.setId(id);
		Result<Project> ret = projectService.get(query);
		if(ret==null || !ret.isSuccess() || ret.getValue()==null){
			return ResultVO.fall("project not exists!");
		}
		Project project = ret.getValue();
		String checkUri = project.getCheckUri();
		String targetPath = project.getTargetPath();
		String onlinehost = project.getOnlineHost();
		String remoteUser = project.getRemoteUser();
		Map result = checkStatus(checkUri, targetPath, onlinehost, remoteUser);
		return ResultVO.success(result);
	}


	private Map checkStatus(String checkUri, String targetPath,
			String onlinehost, String remoteUser) {
		Map result = new HashMap();
		List<String> hosts = processHost(onlinehost);
		for (String host : hosts) {
			Map map = new HashMap();
			String[] hp = host.split(":");
			String checkhost = hp[0];
			try {
				if(checkUri==null || checkUri.trim().equals("")){
					map.put("status", true);
					map.put("msg", "没有配置健康检查");
				}
				String checkUrl = "http://"+checkhost+checkUri;
				String resp = HttpUtils.executeGet(checkUrl);
				if(resp!=null && resp.trim().toUpperCase().contains("SUCCESS")){
					map.put("status", true);
				}else{
					map.put("status", false);
					String tomcat = getTomcat(targetPath);
					String command = "ps -ef | grep '"+tomcat+"' | grep -v grep| awk '{print $2}' ";
					Result<String> retText = shellService.execRemoteShell(command, remoteUser, "", checkhost, Integer.parseInt(hp[1]));
					if(retText.isSuccess() && retText.getValue()!=null){
			        	if(retText.getValue().trim().equals("")){
			        		map.put("msg", "tomcat没起");
			        	}
			        	else{
			        		map.put("msg", "tomcat正常 pid :"+retText.getValue());
			        	}
			        }
					map.put("msg", "null");
				}
				result.put(checkhost, map);
			} catch (Exception e) {
				map.put("status", false);
				result.put(checkhost, map);
			}
		}
		return result;
	}
	
	
	private String getTomcat(String targetPath){
		if(targetPath==null || targetPath.indexOf("tomcat") <= 0){
			return "no tomcat";
		}
		if(targetPath.startsWith("/usr/local/")){
			String tmp = targetPath.substring(11);
			int idx = tmp.indexOf("/");
			if(idx >0 ){
				return tmp.substring(0, idx);
			}
		}
		return "no match tomcat";
	}

	private List<String> processHost(String onlinehost){
		if(onlinehost==null){
			return new ArrayList<String>();
		}
		List<String> list = new ArrayList<String>();
		String[] lines = onlinehost.split("\r\n");
		for (int i = 0; i < lines.length; i++) {
			String hostport = lines[i];
			if(hostport.trim().indexOf(":")>0){
				String[] host = hostport.split(":");
				list.add(host[0]);
			}
		}
		return list;
	}

}
