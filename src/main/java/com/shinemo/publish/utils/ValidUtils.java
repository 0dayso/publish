/**
 * 
 */
package com.shinemo.publish.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.api.util.SpringUtil;
import com.shinemo.publish.client.Apply;
import com.shinemo.publish.client.ApplyQuery;
import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.client.ProjectUser;
import com.shinemo.publish.client.ProjectUserQuery;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.constants.ProjectConstants;
import com.shinemo.publish.service.ApplyService;
import com.shinemo.publish.service.ProjectService;
import com.shinemo.publish.service.ProjectUserService;


/**
 * 
 * @author figo
 * 2016年6月2日
 */
public class ValidUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(ValidUtils.class);

	private static ApplyService applyService = (ApplyService)SpringUtil.getBean("applyService");
	
	private static ProjectService projectService = (ProjectService)SpringUtil.getBean("projectService");
	
	private static ProjectUserService projectUserService = (ProjectUserService)SpringUtil.getBean("projectUserService");
	
	
	
	/**
	 * 检查项目是否存在，并且操作人是否admin
	 * @param projectId
	 * @return
	 */
	public static Result<Project> validProject(Long projectId){
		ProjectQuery pQuery = new ProjectQuery();
		pQuery.setId(projectId);
		Result<Project> rProject = projectService.get(pQuery);
		if(!rProject.isSuccess() || rProject.getValue() == null){
			return new Result<Project>(false,"应用不存在!");
		}
		return rProject;
	}
	
	
	/**
	 * 检查是否存在 （apply & project）
	 * 并判断当前用户是否有权限操作
	 * @param applyId
	 * @return
	 */
	public static Result<Apply> validApply(Long applyId){
		ApplyQuery aQuery = new ApplyQuery();
		aQuery.setId(applyId);;
		Result<Apply> rApply = applyService.get(aQuery);
		if(!rApply.isSuccess() || rApply.getValue()==null){
			return new Result<Apply>(false, "发布单不存在！");
		}
		if(rApply.getValue().getFlag()==ProjectConstants.APPLY_FLAG_DEL){
			return new Result<Apply>(false, "发布单状态不对！");
		}
		
		ProjectQuery pQuery = new ProjectQuery();
		pQuery.setId(rApply.getValue().getProjectId());
		Result<Project> rProject = projectService.get(pQuery);
		if(!rProject.isSuccess() || rProject.getValue()==null){
			return new Result<Apply>(false, "项目不存在！");
		}
		
		Long uid = UserUtil.getUid(Requests.getRequest());
		ProjectUserQuery puQuery = new ProjectUserQuery();
		puQuery.setProjectId(rProject.getValue().getId());
		puQuery.setType(ProjectConstants.PROJECT_USER_TYPE_ADMIN);
		puQuery.setUserId(uid);
		Result<List<ProjectUser>> rUser = projectUserService.find(puQuery);
		if ( uid != rApply.getValue().getUserId()
				&& !UserUtil.isAdmin(Requests.getRequest())
				&& (!rUser.isSuccess() || rUser.getValue() ==null)) {
			return new Result<Apply>(false, "你没有权限操作！");
		}
		return rApply;
	}
	
	
	
	/**
	 * 检查是否存在 （apply & project & apply Status）
	 * 并判断当前用户是否有权限操作
	 * @param applyId
	 * @return
	 */
	public static Result<Apply> validApplyWithStatus(Long applyId){
		ApplyQuery aQuery = new ApplyQuery();
		aQuery.setId(applyId);;
		Result<Apply> rApply = applyService.get(aQuery);
		if(!rApply.isSuccess() || rApply.getValue()==null){
			return new Result<Apply>(false, "发布单不存在！");
		}
		if(rApply.getValue().getFlag()==ProjectConstants.APPLY_FLAG_DEL){
			return new Result<Apply>(false, "发布单状态不对！");
		}
		if(rApply.getValue().getStatus()>=ProjectConstants.APPLY_STATUS_FINISH){
			return new Result<Apply>(false, "发布单已经结束，不能操作！");
		}
		ProjectQuery pQuery = new ProjectQuery();
		pQuery.setId(rApply.getValue().getProjectId());
		Result<Project> rProject = projectService.get(pQuery);
		if(!rProject.isSuccess() || rProject.getValue()==null){
			return new Result<Apply>(false, "项目不存在！");
		}
		
		Long uid = UserUtil.getUid(Requests.getRequest());
		ProjectUserQuery puQuery = new ProjectUserQuery();
		puQuery.setProjectId(rProject.getValue().getId());
		puQuery.setType(ProjectConstants.PROJECT_USER_TYPE_ADMIN);
		puQuery.setUserId(uid);
		Result<List<ProjectUser>> rUser = projectUserService.find(puQuery);
		if ( uid != rApply.getValue().getUserId()
				&& !UserUtil.isAdmin(Requests.getRequest())
				&& (!rUser.isSuccess() || rUser.getValue() ==null)) {
			return new Result<Apply>(false, "你没有权限操作！");
		}
		return rApply;
	}

}
