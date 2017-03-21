package com.shinemo.publish.resources.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.common.BaseResource;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.service.ProjectService;
import com.shinemo.publish.utils.FileSearcher;

@RequestMapping("/debugger")
@Controller
public class DebugResource extends BaseResource {

	@Resource
	private ProjectService projectService;
	
	//代码根目录
	private final static String defaultRoot = "/home/admin/localgit/";
	

	@RequestMapping(value = "/projects")
	@ResponseBody
	public ResultVO<List<Project>> projects(@Context HttpServletRequest request) {
		ProjectQuery query = new ProjectQuery();
		query.setStat(1);
		
		Result<List<Project>> ret = projectService.find(query);
		if (ret.isSuccess() ) {
			Result<List<Project>> result = new Result<List<Project>>(true, ret.getValue(), 200, null);
			List<Project> list = result.getValue();
			List<Project> projects = new ArrayList<Project>();
			for (Project project : list) {
				if(StringUtils.isEmpty(project.getOnlineHost()) || StringUtils.isEmpty(project.getCheckUri())){
					continue;
				}
				Project e = new Project();
				e.setId(project.getId());
				e.setName(project.getName());
				e.setTitle(project.getTitle());
				List<String> hosts = new ArrayList<String>();
				String hostinfo = project.getOnlineHost();
				String[] lines = hostinfo.split("\r\n");
				for (String hostPort : lines) {
					String[] hp = hostPort.split(":");
					hosts.add(hp[0]);
				}
				e.setDebugHost(hosts);
				String checkUri = project.getCheckUri();
				int port = getDebugPort(checkUri);
				e.setDebugPort(port);
				
				projects.add(e);
			}
			return ResultVO.success(projects);
		}
		return ResultVO.fall(200, Errors.ERROR_500.getMsg());
	}
	
	private int getDebugPort(String checkUri){
		int start = checkUri.indexOf(":");
		int end = checkUri.indexOf("/");
		int port = Integer.parseInt(checkUri.substring(start+1, end));
		return port+700;
	}
	
	
	@RequestMapping(value = "/searchjava")
	@ResponseBody
	public ResultVO<List<String>> searchJava(@Context HttpServletRequest request,
			@QueryParam("projectId") long projectId,
			@QueryParam("keyword") String keyword){
		if(keyword == null || keyword.trim().length() < 5){
			return ResultVO.fall(200, "keyword too short!");
		}
		ProjectQuery query = new ProjectQuery();
		query.setId(projectId);;
		Result<Project> result = projectService.get(query);
		if(result.isSuccess()){
			String sourceRoot = result.getValue().getSourcePath();
			File[] files = FileSearcher.searchFile(sourceRoot, keyword);
			List<String> l = new ArrayList<String>();
			for (File file : files) {
				l.add(file.getAbsolutePath());
			}
			return ResultVO.success(l);
		}
		
		return ResultVO.fall(200, Errors.ERROR_500.getMsg());
	}
	
	
	
	@RequestMapping(value = "/getclazz")
	@ResponseBody
	public ResultVO<Map<String,String>> searchJava(@Context HttpServletRequest request,
			@QueryParam("filename") String filename){
		if(filename == null || filename.trim().length() < 5){
			return ResultVO.fall(200, "filename too short!");
		}
		Map<String,String> map = new  HashMap<String,String>();
		map.put("package", FileSearcher.getClazzName(filename));
		map.put("content", FileSearcher.getFile(filename));
		return ResultVO.success(map);
	}
	
}
