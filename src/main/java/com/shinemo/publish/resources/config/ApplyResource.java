package com.shinemo.publish.resources.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.Aace.ItfPacker.PackData;
import com.shinemo.Aace.groupadmin.client.GroupAdminClient;
import com.shinemo.Aace.msgstruct.model.ImMessage;
import com.shinemo.publish.client.Apply;
import com.shinemo.publish.client.ApplyLog;
import com.shinemo.publish.client.ApplyLogQuery;
import com.shinemo.publish.client.ApplyQuery;
import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.client.ProjectUser;
import com.shinemo.publish.client.ProjectUserQuery;
import com.shinemo.publish.common.BaseResource;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.constants.ProjectConstants;
import com.shinemo.publish.service.ApplyLogService;
import com.shinemo.publish.service.ApplyService;
import com.shinemo.publish.service.ProjectService;
import com.shinemo.publish.service.ProjectUserService;
import com.shinemo.publish.service.ShellService;
import com.shinemo.publish.utils.DateUtils;
import com.shinemo.publish.utils.MailUtil;
import com.shinemo.publish.utils.Requests;
import com.shinemo.publish.utils.ServerConfig;
import com.shinemo.publish.utils.UserUtil;
import com.shinemo.publish.utils.ValidUtils;

@RequestMapping("/apply")
@Controller
public class ApplyResource extends BaseResource {

	private String ID = "id";
	private String TITLE = "title";		//发布单标题
	private String GITBRANCH = "gitBranch";
	private String USERID = "userId";
	private String FLAG = "flag";
	private String STATUS = "status";
	private String PROJECTID = "projectId";
	private String LOG = "log";
	private String IDX = "idx";			//发布到第几台
	private String GITVERSION = "gitVersion";	//git version
	private String FILE_LIST = "fileList";

	@Resource
	private ProjectUserService projectUserService;

	@Resource
	private ProjectService projectService;

	@Resource
	private ApplyService applyService;
	
	@Resource
	private ShellService shellService;

	@Resource
	private ApplyLogService applyLogService;
	
	@Resource
	private GroupAdminClient groupAdminClient;
	
	//接收通知的群号  6146112（测试的）
	private static long groupId = 2972520;  //讯盟技术群
	
	/** 彩云消息 **/
	private void sendMsg(String type,String pName,String title,String userName){
			
			String name = "发布大哥";
			ImMessage msg = new ImMessage();
			msg.setType(1);
			StringBuffer sb = new StringBuffer();
			sb.append(userName).append("完成一个发布\n");
			sb.append("type：").append(type).append("\n");
			sb.append("module：").append(pName).append("\n");
			sb.append("feature：").append(title).append("\n");
			sb.append(DateUtils.format2str(new Date()));
			msg.setMessage(sb.toString().getBytes());
			msg.setUserName(name);
			byte[] message = PackData.struct2String(msg);
			groupAdminClient.sendMsg("10001", groupId, 1, message, false);
			
		}

	@RequestMapping(value = "/getUserApplys")
	@ResponseBody
	public ResultVO getCurrUserApplys(@Context HttpServletRequest request,
			@DefaultValue("1") @QueryParam("pageCount") int pageCount,
			@DefaultValue("20") @QueryParam("pageSize") int pageSize) {
		long userId = UserUtil.getUid(request);
		ApplyQuery query = new ApplyQuery();
		query.setUserId(userId);
		query.setCurrentPage(pageCount);
		query.setPageSize(pageSize);
		Result<List<Apply>> ret = applyService.find(query);
		ApplyQuery queryc = new ApplyQuery();
		queryc.setUserId(userId);
		Result<Long> rcount = applyService.count(queryc);
		if (ret.isSuccess() && rcount.isSuccess()) {
			Map map = new HashMap();
			map.put("count", rcount.getValue());
			map.put("list", ret.getValue());
			Result<Map> result = new Result<Map>(true, map, 200, null);
			return buildResultVO(result);
		}
		return buildResultVO(ret);
	}

	@RequestMapping(value = "/search")
	@ResponseBody
	public ResultVO search(
			@Context HttpServletRequest request,
			@RequestParam(value = "pageCount", defaultValue = "1") int pageCount,
			@RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
		Long userId = Requests.getLong("userId");
		Long projectId = Requests.getLong("projectId");
		String startDate = Requests.getString("startDate");
		String endDate = Requests.getString("endDate");
		ApplyQuery query = new ApplyQuery();
		query.setCurrentPage(pageCount);
		query.setPageSize(pageSize);
		if(projectId!=null)query.setProjectId(projectId);
		if(userId!=null)query.setUserId(userId);
		if(startDate!=null)query.setStartDate(DateUtils.formatDate(startDate));
		if(endDate!=null)query.setEndDate(DateUtils.formatDate(endDate));
		Result<List<Apply>> ret = applyService.find(query);
		Result<Long> rcount = applyService.count(query);
		if (ret.isSuccess() && rcount.isSuccess()) {
			Map map = new HashMap();
			map.put("count", rcount.getValue());
			map.put("list", ret.getValue());
			Result<Map> result = new Result<Map>(true, map, 200, null);
			return buildResultVO(result);
		}
		return ResultVO.fall(Errors.E_UN_KNOWN);
	}
	
	@RequestMapping(value = "/getApplys")
	@ResponseBody
	public ResultVO getApplys(
			@Context HttpServletRequest request,
			@RequestParam(value = "pageCount", defaultValue = "1") int pageCount,
			@RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
		long userId = UserUtil.getUid(request);

		// 系统管理员查看所有 TODO
		if (UserUtil.isAdmin(request)) {
			ApplyQuery query = new ApplyQuery();
			query.setCurrentPage(pageCount);
			query.setPageSize(pageSize);
			query.setStatus(ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY);
			//query.setFlag(ProjectConstants.APPLY_FLAG_UNAUDITED);
			Result<List<Apply>> ret = applyService.find(query);
			ApplyQuery queryc = new ApplyQuery();
			queryc.setStatus(ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY);
			//queryc.setFlag(ProjectConstants.APPLY_FLAG_UNAUDITED);
			Result<Long> rcount = applyService.count(queryc);
			if (ret.isSuccess() && rcount.isSuccess()) {
				Map map = new HashMap();
				map.put("count", rcount.getValue());
				map.put("list", ret.getValue());
				Result<Map> result = new Result<Map>(true, map, 200, null);
				return buildResultVO(result);
			}
		}

		// 拥有几个作为管理员的项目 TODO
		Result<List<Apply>> ret = applyService.getNewApplys(userId, pageCount,
				pageSize);
		Result<Long> rcount = applyService.getNewApplysCount(userId);
		if (ret.isSuccess() && rcount.isSuccess()) {
			Map map = new HashMap();
			map.put("count", rcount.getValue());
			map.put("list", ret.getValue());
			Result<Map> result = new Result<Map>(true, map, 200, null);
			return buildResultVO(result);
		}
		return ResultVO.fall(Errors.E_UN_KNOWN);
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public ResultVO<Apply> get(@QueryParam("id") long id) {
		ApplyQuery query = new ApplyQuery();
		query.setId(id);
		Result<Apply> ret = applyService.get(query);
		return buildResultVO(ret);
	}
	
	
	@RequestMapping(value = "/getAll")
	@ResponseBody
	public ResultVO<Apply> getAll(@QueryParam("id") long id) {
		ApplyQuery query = new ApplyQuery();
		query.setId(id);
		Result<Apply> ret = applyService.get(query);
		if(ret.isSuccess() && ret.getValue()!=null){
			Long projectId=ret.getValue().getProjectId();
			ProjectQuery pq = new ProjectQuery();
			pq.setId(projectId);
			Result<Project> pret = projectService.get(pq);
			Map map = new HashMap();
			map.put("apply", ret.getValue());
			map.put("project", pret.getValue());
			return buildResultVO(new Result().setSuccess(true).setValue(map));
		}
		return buildResultVO(ret);
	}
	
	

	@RequestMapping(value = "/add")
	@ResponseBody
	public ResultVO<Long> add(@Context HttpServletRequest request) {

		Long projectId = Requests.getLong(PROJECTID);
		Long userId = UserUtil.getUid(request);
		
		
		ProjectQuery query = new ProjectQuery();
		query.setId(projectId);
		Result<Project> pret = projectService.get(query);
		if(!pret.isSuccess()){
			return ResultVO.<Long> fall(Errors.ERROR_500);
		}
		Project project = pret.getValue();
		
		ProjectUserQuery pquery = new ProjectUserQuery();
		pquery.setProjectId(projectId);
		pquery.setUserId(userId);
		Result<ProjectUser> puRet = projectUserService.get(pquery);
		if (!puRet.isSuccess()) {
			return ResultVO.<Long> fall(Errors.ERROR_500);
		} else if (puRet.getValue() == null) {
			return ResultVO.<Long> fall(Errors.E_INVALIDUSER);
		}

		ProjectUser pu = puRet.getValue();
		Apply a = new Apply();
		
		a = getApplyFromReq(request, a);
		a.setProjectId(pu.getProjectId());
		a.setUserId(pu.getUserId());
		a.setFlag(ProjectConstants.APPLY_FLAG_NORMAL);
		a.setLog("init \n");
		if(project.getNeedApproval()==1){
			a.setFlag(ProjectConstants.APPLY_FLAG_UNAUDITED);
		}
		a.setStatus(ProjectConstants.APPLY_STATUS_START);

		Result<Long> ret = applyService.add(a);
		applyLogService.addLog(ret.getValue(),ProjectConstants.APPLY_STATUS_START, "创建成功");
		return buildResultVO(ret);
	}

	/**
	 * 包含修改审核通过
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit")
	@ResponseBody
	public ResultVO<Boolean> edit(@Context HttpServletRequest request,
			@QueryParam("id") long id) {

		ApplyQuery query = new ApplyQuery();
		query.setId(id);
		Result<Apply> applyRet = applyService.get(query);
		Apply a = new Apply();
		if (applyRet.isSuccess()) {
			a = applyRet.getValue();
		}
		Integer status = a.getStatus();
		// 自己  系统管理员  项目管理员
		ProjectUserQuery puq = new ProjectUserQuery();
		puq.setUserId(UserUtil.getUid(request));
		puq.setProjectId(a.getProjectId());
		puq.setType(ProjectConstants.PROJECT_USER_TYPE_ADMIN);
		Result<ProjectUser> pur = projectUserService.get(puq);

		if (a.getUserId() != UserUtil.getUid(request)
				&& !UserUtil.isAdmin(request)
				&& (!pur.isSuccess() || pur.getValue() == null)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		Apply tmp = getApplyFromReq(request, a);
		Result<Boolean> ret = applyService.update(tmp);
		if (ret.isSuccess()) {
			applyLogService.addLog(id,status, "修改发布单信息");
		}
		return buildResultVO(ret);
	}
	
	
	//提交上线
	@RequestMapping(value = "/submit")
	@ResponseBody
	public ResultVO submit(@Context HttpServletRequest request,
			@QueryParam("id") long id) {

		ApplyQuery query = new ApplyQuery();
		query.setId(id);
		Result<Apply> applyRet = applyService.get(query);
		Apply a = new Apply();
		if (applyRet.isSuccess()) {
			a = applyRet.getValue();
		}
		
		//自己
		if (a.getUserId() != UserUtil.getUid(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		//状态判断
		if (a.getStatus()>=ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY ||
				a.getFlag() != ProjectConstants.APPLY_FLAG_UNAUDITED) {
			return ResultVO.fall(Errors.E_STATUS_INVALID);
		}
		
		updateProcess(id, ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY, "", "提交上线申请");
		MailUtil.sendSubmitMail(id);
		return ResultVO.success("success");
	}
	
	
		//管理员审核
		/**
		 * 
		 * @param request
		 * @param id
		 * @param flag 1审核通过、2不通过
		 * @return
		 */
		@RequestMapping(value = "/audit")
		@ResponseBody
		public ResultVO<Boolean> audit(@Context HttpServletRequest request,
				@QueryParam("id") long id,@QueryParam("flag") int flag) {

			ApplyQuery query = new ApplyQuery();
			query.setId(id);
			Result<Apply> applyRet = applyService.get(query);
			Apply a = new Apply();
			if (applyRet.isSuccess()) {
				a = applyRet.getValue();
			}
			
			//状态判断
			if (a.getStatus()!=ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY ||
					a.getFlag() != ProjectConstants.APPLY_FLAG_UNAUDITED) {
				return ResultVO.fall(Errors.E_STATUS_INVALID);
			}
			
			a.setFlag(flag);
			Result<Boolean> ret = applyService.update(a);
			if (ret.isSuccess()) {
				if(flag==ProjectConstants.APPLY_FLAG_NORMAL){
					applyLogService.addLog(id,ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY, "上线审核通过");
				}else if(flag==ProjectConstants.APPLY_FLAG_AUDIT_NO_PASS){
					applyLogService.addLog(id,ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY, "上线审核不通过");
				}else{
					applyLogService.addLog(id,ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY, "审核状态错乱");
				}
				
			}
			return buildResultVO(ret);
		}

	@RequestMapping(value = "/detail")
	@ResponseBody
	public ResultVO detail(@QueryParam("id") long id) {
		ApplyLogQuery query = new ApplyLogQuery();
		query.setApplyId(id);
		Result<List<ApplyLog>> ret = applyLogService.find(query);
		
		ApplyQuery aquery = new ApplyQuery();
		query.setId(id);
		Result<Apply> applyRet = applyService.get(aquery);
		Map<String,Object> map = new HashMap<String,Object>();
		if(applyRet.isSuccess() && ret.isSuccess()){
			map.put("apply", applyRet.getValue());
			map.put("applylog", ret.getValue());
			Result<Map<String,Object>> result = new Result<Map<String,Object>>();
			result.setSuccess(true).setValue(map);
			return buildResultVO(result);
		}
		return ResultVO.fall(Errors.E_UN_KNOWN);
	}

	@RequestMapping(value = "/del")
	@ResponseBody
	public ResultVO<Boolean> del(@Context HttpServletRequest request,
			@QueryParam("id") long id) {
		long userId = UserUtil.getUid(request);
		// 自己||管理员 才能删除
		ApplyQuery query = new ApplyQuery();
		query.setId(id);
		Result<Apply> ret = applyService.get(query);
		if (ret.isSuccess()) {
			Apply a = ret.getValue();
			if (a == null) {
				return ResultVO.<Boolean> fall(Errors.E_NOTEXIST);
			}
			if (userId != a.getUserId()) {
				return ResultVO.<Boolean> fall(Errors.E_INVALIDUSER);
			}
			a.setFlag(ProjectConstants.APPLY_FLAG_DEL);
			Result<Boolean> r = applyService.update(a);
			applyLogService.addLog(id,a.getStatus(), "删除发布单");
			return buildResultVO(r);
		}
		return ResultVO.<Boolean> fall(Errors.ERROR_500);
	}
	
	@RequestMapping(value = "/finish")
	@ResponseBody
	public ResultVO finish(@Context HttpServletRequest request,@QueryParam("id") long id) {
		Result<Apply> retA = ValidUtils.validApply(id);
		if(!retA.isSuccess()){
			return ResultVO.fall(retA.getMsg());
		}
		Apply apply = retA.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project project = projectService.get(query).getValue();
		
		String cmdpath = ServerConfig.get("shell.path");
		String userName = UserUtil.getUserName(request);
		String tagName = userName+"-"+apply.getTitle()+"-publish";
		String cmd =  cmdpath+ "/gittag.sh "
				+ project.getAppName() + " " + project.getSourcePath() + " " +tagName ;
		
		Result<String> result = shellService.execLocalShell(cmd);
		apply.setStatus(ProjectConstants.APPLY_STATUS_FINISH);
		updateProcess(id, ProjectConstants.APPLY_STATUS_FINISH, "finish", "完成发布单");
		applyService.update(apply);
		MailUtil.sendNotifyMail(id);
		sendMsg(project.getType()==1?"前端":"Java", project.getTitle(), apply.getTitle(), userName);
		return ResultVO.success("ok");
	}
	
	/**
	 * 查看发布机本地文件与目录
	 * @param request
	 * @param id
	 * @param dir
	 * @return
	 */
	@RequestMapping(value = "/ls")
	@ResponseBody
	public ResultVO ls(@Context HttpServletRequest request,@QueryParam("id") long id) {
		ProjectQuery query = new ProjectQuery();
		query.setId(id);
		Project project = projectService.get(query).getValue();
		String basedir = project.getFileName();
		String dir = Requests.getString("dir");
		if(!StringUtils.isEmpty(dir)){
			basedir += "/"+dir;
		}
		File tmpdir = new File(basedir);
		File[] files = tmpdir.listFiles();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		for (File file : files) {
			Map map = new HashMap();
			if(file.isDirectory()){
				map.put("name", file.getName());
				map.put("type", "dir");
			}else{
				map.put("name", file.getName());
				map.put("type", "file");
			}
			list.add(map);
		}
		return ResultVO.success(list);
	}
	
	
	/**
	 * 获取最近三天内统一git仓库下是否有发布单未结束
	 * @param request
	 * @param projectId
	 * @return
	 */
	@RequestMapping(value = "/projectInfo")
	@ResponseBody
	public ResultVO projectInfo(@Context HttpServletRequest request,@QueryParam("projectId") long projectId) {
		ProjectQuery query = new ProjectQuery();
		query.setId(projectId);
		Result<Project> r = projectService.get(query);
		Project p = r.getValue();
		Result<Integer> result = new Result<Integer>();
		result.setSuccess(true);
		result.setValue(r.getValue().getType());
		//监测下是否有其他人在发布状态中，如果有就提醒
		Result<List<Apply>> r2 = applyService.getProcessingApply(projectId,DateUtils.getDayPlus(-3));
		if(r2!=null && r2.getValue()!=null && r2.getValue().size() > 0){
			return ResultVO.fall("同一git库下最近三天有发布单未结束，保证代码同步，请谨慎操作！");
			//result.setMsg("同一git库下最近三天有发布单未结束，保证代码同步，请谨慎操作！");
		}else{
			result.setMsg("ok");
		}
		ResultVO rv = buildResultVO(result);
		rv.setMsg(result.getMsg());
		return rv;
	}
	
	
	
	
	private Apply getApplyFromReq(HttpServletRequest request, Apply a) {
		a.setFlag(Requests.getInt(FLAG));
		a.setGitBranch(Requests.getString(GITBRANCH));
		a.setLog(Requests.getString(LOG));
		a.setProjectId(Requests.getLong(PROJECTID));
		a.setStatus(Requests.getInt(STATUS));
		a.setUserId(Requests.getLong(USERID));
		a.setId(Requests.getLong(ID));
		a.setTitle(Requests.getString(TITLE));
		a.setGitVersion(Requests.getString(GITVERSION));
		a.setIdx(Requests.getInt(IDX));
		a.setFileList(Requests.getString(FILE_LIST));
		return a;
	}
	
	
	/**
	 * 更新每一步的进度信息
	 * 
	 * @param appId
	 * @param status
	 * @param log
	 */
	private void updateProcess(long applyId, int status, String log,
			String stepName) {
		ApplyQuery aq = new ApplyQuery();
		aq.setId(applyId);
		Result<Apply> ret = applyService.get(aq);
		if (ret.isSuccess()) {
			Apply apply = ret.getValue();
			apply.setStatus(status);
			apply.setLog(log);
			applyService.update(apply);

			ApplyLog applylog = new ApplyLog();
			applylog.setApplyId(applyId);
			applylog.setLog(log);
			applylog.setStep(status);
			applylog.setStepName(stepName);
			applylog.setUserId(UserUtil.getUid(Requests.getRequest()));
			applylog.setUsername(UserUtil.getUserName(Requests.getRequest()));
			applyLogService.add(applylog);
		}
	}
	
	
}
