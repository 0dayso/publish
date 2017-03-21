package com.shinemo.publish.resources.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.publish.client.EnvEnum;
import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.BaseResource;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.constants.ProjectConstants;
import com.shinemo.publish.service.ProjectService;
import com.shinemo.publish.utils.Requests;
import com.shinemo.publish.utils.UserUtil;

@RequestMapping("/project")
@Controller
public class ProjectResource extends BaseResource {

	private String ID = "id";
	private String TITLE = "title";
	private String NAME = "name"; // 工程名字
	private String DESCR = "descr"; // 描述
	private String GIT = "git";
	private String PREHOST = "preHost"; // 预发机器，端口
	private String ONLINEHOST = "onlineHost"; // 机器列表：端口
	private String OWNERID = "ownerId"; // 开发负责人
	private String TESTID = "testId"; // 测试负责人
	private String TYPE = "type"; // 0:java 1:静态资源
	private String SOURCEPATH = "sourcePath"; // 源代码路径
	private String TARGETPATH = "targetPath"; // 目标机器文件位置
	private String FILENAME = "fileName"; // 要同步的文件，可以是目录
	private String BUILDLOG = "buildLog";
	private String APPLOG = "appLog";
	private String BEFORESYNC = "beforeSync"; // 同步文件前执行的脚本
	private String AFTERSYNC = "afterSync"; // 同步文件后执行的脚本
	private String REMOTEUSER = "remoteUser"; // 目标机器登录用户
	private String NEEDAPPROVAL = "needApproval"; // 是否需要审批才能发布
	private String PRE_BUILD = "preBuild";
	private String ONLINE_BUILD = "onlineBuild";
	private String ENV = "env";
	private String CHECK_URI = "checkUri";	//健康检查路径

	@Resource
	private ProjectService projectService;

	@RequestMapping(value = "/get")
	@ResponseBody
	public ResultVO<Project> get(@Context HttpServletRequest request,
			@QueryParam("id") long id) {
		if (!UserUtil.isAdmin(request)) {
			return ResultVO.<Project> fall(Errors.E_NO_AUTH);
		}
		ProjectQuery query = new ProjectQuery();
		query.setId(id);
		Result<Project> ret = projectService.get(query);
		return buildResultVO(ret);
	}

	@RequestMapping(value = "/list")
	@ResponseBody
	public ResultVO list(
			@Context HttpServletRequest request,
			@RequestParam(value = "pageCount", defaultValue = "1") int pageCount,
			@RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
		if (!UserUtil.isAdmin(request)) {
			return ResultVO.<List<Project>> fall(Errors.E_NO_AUTH);
		}
		
		int type = Requests.getInt("type",-1);
		String title = Requests.getString("title","");
		
		ProjectQuery query = new ProjectQuery();
		query.setCurrentPage(pageCount);
		query.setPageSize(pageSize);
		if(type != -1){
			query.setType(type);
		}
		if(!title.equals("")){
			query.setTitle(title);
		}
		
		Result<List<Project>> ret = projectService.find(query);
		ProjectQuery queryc = new ProjectQuery();
		if(type != -1){
			queryc.setType(type);
		}
		if(!title.equals("")){
			queryc.setTitle(title);
		}
		Result<Long> rcount = projectService.count(queryc);
		if (ret.isSuccess() && rcount.isSuccess()) {
			Map map = new HashMap();
			map.put("count", rcount.getValue());
			map.put("list", ret.getValue());
			Result<Map> result = new Result<Map>(true, map, 200, null);
			return buildResultVO(result);
		}

		return ResultVO.fall(200, Errors.ERROR_500.getMsg());
	}

	@RequestMapping(value = "/selectAll", method = RequestMethod.GET)
	@ResponseBody
	public ResultVO selectAll(@Context HttpServletRequest request) {
		Long userId = UserUtil.getUid(request);
		boolean self = Requests.getBoolean("self", true);
		if (self) {
			Result<List<Project>> ret = projectService.listByUid(userId, 100);
			if (ret.isSuccess()) {
				List rest = new ArrayList();
				List<Project> projectList = ret.getValue();
				if (projectList != null) {
					for (Project project : projectList) {
						Map map = new HashMap();
						String envName = EnvEnum.getName(project.getEnv());
						if(!StringUtils.isEmpty(envName)){
							map.put("text", "("+envName+")"+project.getTitle());
						}else{
							map.put("text", project.getTitle());
						}
						map.put("value", project.getId());
						rest.add(map);
					}
				}

				Result<List> result = new Result<List>(true, rest, 200, null);
				return buildResultVO(result);
			}
		}else{
			//pull all project
			ProjectQuery query = new ProjectQuery();
			query.setPageSize(100);
			Result<List<Project>> ret = projectService.find(query);
			if (ret.isSuccess()) {
				List rest = new ArrayList();
				List<Project> projectList = ret.getValue();
				if (projectList != null) {
					for (Project project : projectList) {
						Map map = new HashMap();
						map.put("text", project.getTitle());
						map.put("value", project.getId());
						rest.add(map);
					}
				}
				Result<List> result = new Result<List>(true, rest, 200, null);
				return buildResultVO(result);
			}
		}
		return ResultVO.fall(200, Errors.ERROR_500.getMsg());
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public ResultVO<Long> add(@Context HttpServletRequest request) {
		if (!UserUtil.isAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		Project p = new Project();
		p.setType(ProjectConstants.PROJECT_TYPE_JAVA);
		p = getProjectFromReq(request, p);
		Result<Long> ret = projectService.add(p);
		return buildResultVO(ret);
	}

	@RequestMapping(value = "/edit")
	@ResponseBody
	public ResultVO<Boolean> edit(@Context HttpServletRequest request,
			@QueryParam("id") long id) {
		if (!UserUtil.isAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		Project p = new Project();
		ProjectQuery query = new ProjectQuery();
		query.setId(id);
		Result<Project> result = projectService.get(query);
		if (result.isSuccess()) {
			p = result.getValue();
		} else {
			return ResultVO.fall(Errors.E_NOTEXIST);
		}
		p = getProjectFromReq(request, p);
		Result<Boolean> ret = projectService.update(p);
		return buildResultVO(ret);
	}

	@RequestMapping(value = "/copy")
	@ResponseBody
	public ResultVO<Long> copy(@Context HttpServletRequest request,
			@QueryParam("id") long id) {
		if (!UserUtil.isAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		Project p = new Project();
		ProjectQuery query = new ProjectQuery();
		query.setId(id);
		Result<Project> result = projectService.get(query);
		if (result.isSuccess()) {
			p = result.getValue();
		} else {
			return ResultVO.fall(Errors.E_NOTEXIST);
		}
		p.setId(null);
		p.setTitle(p.getTitle() + "_copy");
		Result<Long> ret = projectService.add(p);
		return buildResultVO(ret);
	}

	@RequestMapping(value = "/del")
	@ResponseBody
	public ResultVO<Boolean> del(@Context HttpServletRequest request,
			@QueryParam("id") long id) {
		if (!UserUtil.isAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		long uid = UserUtil.getUid(request);
		Project p = new Project();
		ProjectQuery query = new ProjectQuery();
		query.setId(id);
		Result<Boolean> result = projectService.delete(query);
		if (result.isSuccess()) {
			return buildResultVO(result);
		} else {
			return ResultVO.fall(Errors.E_NOTEXIST);
		}
	}

	private Project getProjectFromReq(HttpServletRequest request, Project p) {
		p.setAfterSync(Requests.getString(AFTERSYNC));
		p.setAppLog(Requests.getString(APPLOG));
		p.setBeforeSync(Requests.getString(BEFORESYNC));
		p.setBuildLog(Requests.getString(BUILDLOG));
		p.setDescr(Requests.getString(DESCR));
		p.setFileName(Requests.getString(FILENAME));
		p.setGit(Requests.getString(GIT));
		p.setId(Requests.getLong(ID));
		p.setName(Requests.getString(NAME));
		p.setOnlineHost(Requests.getString(ONLINEHOST));
		p.setOwnerId(Requests.getLong(OWNERID));
		p.setPreHost(Requests.getString(PREHOST));
		p.setSourcePath(Requests.getString(SOURCEPATH));
		p.setTargetPath(Requests.getString(TARGETPATH));
		p.setTestId(Requests.getLong(TESTID));
		p.setTitle(Requests.getString(TITLE));
		p.setType(Requests.getInt(TYPE));
		p.setRemoteUser(Requests.getString(REMOTEUSER));
		p.setNeedApproval(Requests.getInt(NEEDAPPROVAL));
		p.setPreBuild(Requests.getString(PRE_BUILD));
		p.setOnlineBuild(Requests.getString(ONLINE_BUILD));
		p.setEnv(Requests.getInt(ENV,-1));
		p.setCheckUri(Requests.getString(CHECK_URI));
		return p;
	}

}
