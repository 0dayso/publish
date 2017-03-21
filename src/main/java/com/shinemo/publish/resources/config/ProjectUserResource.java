package com.shinemo.publish.resources.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.publish.client.ProjectUser;
import com.shinemo.publish.client.ProjectUserQuery;
import com.shinemo.publish.common.BaseResource;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.service.ProjectUserService;
import com.shinemo.publish.utils.Requests;
import com.shinemo.publish.utils.UserUtil;


@RequestMapping("/projectuser")
@Controller
public class ProjectUserResource extends BaseResource {
	
	private String ID = "id";
	private String PROJECTID = "projectId";
	private String USERID = "userId";
	private String TYPE = "type";	//0：普通  1：管理员
	
	
	@Resource
    private ProjectUserService projectUserService;

    
	@RequestMapping(value="/getProjectUsers")
	@ResponseBody
    public ResultVO<List<ProjectUser>> getProjectUsers(@Context HttpServletRequest request,@QueryParam("projectId") long projectId) {
		if(!UserUtil.isAdmin(request)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		ProjectUserQuery query = new ProjectUserQuery();
		query.setProjectId(projectId);
    	Result<List<ProjectUser>> ret = projectUserService.find(query);
    	return buildResultVO(ret);
    }
	
	
	@RequestMapping(value="/getUserProjects")
	@ResponseBody
    public ResultVO<List<ProjectUser>> getUserProjects(@Context HttpServletRequest request,@QueryParam("userId") long userId) {
		if(!UserUtil.isAdmin(request)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		ProjectUserQuery query = new ProjectUserQuery();
		query.setUserId(userId);
    	Result<List<ProjectUser>> ret = projectUserService.find(query);
    	return buildResultVO(ret);
    }
	
	
	@RequestMapping(value="/get")
	@ResponseBody
    public ResultVO<ProjectUser> get(@Context HttpServletRequest request,@QueryParam("id") long id) {
		if(!UserUtil.isAdmin(request)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		ProjectUserQuery query = new ProjectUserQuery();
		query.setId(id);
    	Result<ProjectUser> ret = projectUserService.get(query);
    	return buildResultVO(ret);
    }
	
	
	@RequestMapping(value="/add")
	@ResponseBody
    public ResultVO add(@Context HttpServletRequest request) {
		if(!UserUtil.isAdmin(request)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		ProjectUser p = new ProjectUser();
		getProjectUserFromReq(request, p);
    	Result<Long> ret = projectUserService.add(p);
    	return buildResultVO(ret);
    }
	
	@RequestMapping(value="/edit")
	@ResponseBody
    public ResultVO edit(@Context HttpServletRequest request) {
		if(!UserUtil.isAdmin(request)){
				return ResultVO.fall(Errors.E_NO_AUTH);
		}
		ProjectUser p = new ProjectUser();
		getProjectUserFromReq(request, p);
    	Result<Boolean> ret = projectUserService.update(p);
    	return buildResultVO(ret);
    }
	
	
	/**
	 * userIds=1,2,3
	 * projectId=1
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/addBatch")
	@ResponseBody
    public ResultVO addBatch(@Context HttpServletRequest request) {
		if(!UserUtil.isAdmin(request)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		List<ProjectUser> pusers = getProjectUsersFromReq(request);
		for (ProjectUser projectUser : pusers) {
			ProjectUserQuery query = new ProjectUserQuery();
			query.setUserId(projectUser.getUserId());
			query.setProjectId(projectUser.getProjectId());
			Result<ProjectUser> ret = projectUserService.get(query);
			if(!ret.isSuccess() || ret.getValue() == null){
				projectUserService.add(projectUser);
			}
		}
		return buildResultVO(new Result(true, "success"));
    }
	
	@RequestMapping(value="/del")
	@ResponseBody
    public ResultVO del(@Context HttpServletRequest request,@QueryParam("userId") long userId,@QueryParam("projectId") long projectId) {
		if(!UserUtil.isAdmin(request)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		ProjectUserQuery query = new ProjectUserQuery();
		query.setProjectId(projectId);
		query.setUserId(userId);
		Result<ProjectUser> pu = projectUserService.get(query);
		if(pu.isSuccess() && pu.getValue()!=null){
			ProjectUserQuery tq = new ProjectUserQuery();
			tq.setId(pu.getValue().getId());
			Result<Boolean> ret =projectUserService.delete(tq);
			return buildResultVO(ret);
		}
    	return ResultVO.fall(200, "不存在");
    }
    
	
	
	private ProjectUser getProjectUserFromReq(HttpServletRequest request,ProjectUser p){
		p.setId(Requests.getLong(ID));
		p.setProjectId(Requests.getLong(PROJECTID));
		p.setUserId(Requests.getLong(USERID));
		p.setType(Requests.getInt(TYPE));
		return p;
	}
	
	
	private List<ProjectUser> getProjectUsersFromReq(HttpServletRequest request){
		String idTmp = request.getParameter("userIds");
		String[] userIds = idTmp.split(",");
		List<ProjectUser> pusers = new ArrayList<ProjectUser>();
		if(userIds!=null){
			for (String userIdStr : userIds) {
				long userId = Long.parseLong(userIdStr);
				ProjectUser p = new ProjectUser();
				p.setId(Requests.getLong(ID));
				p.setProjectId(Requests.getLong(PROJECTID));
				p.setUserId(userId);
				p.setType(Requests.getInt(TYPE));
				pusers.add(p);
			}
		}
		return pusers;
	}
   
}
