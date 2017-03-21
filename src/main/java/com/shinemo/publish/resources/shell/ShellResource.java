package com.shinemo.publish.resources.shell;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.publish.client.Apply;
import com.shinemo.publish.client.ApplyLog;
import com.shinemo.publish.client.ApplyQuery;
import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
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
import com.shinemo.publish.utils.HttpUtils;
import com.shinemo.publish.utils.Requests;
import com.shinemo.publish.utils.ServerConfig;
import com.shinemo.publish.utils.UserUtil;
import com.shinemo.publish.utils.ValidUtils;

@RequestMapping("/shell")
@Controller
public class ShellResource extends BaseResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(ShellResource.class);

	@Resource
	private ProjectService projectService;

	@Resource
	private ApplyService applyService;

	@Resource
	private ApplyLogService applyLogService;

	@Resource
	private ShellService shellService;
	

	@Resource
	private ProjectUserService projectUserService;
	
	@RequestMapping(value = "/checkShell")
	@ResponseBody
	public ResultVO<String> checkShell(@Context HttpServletRequest request,
			@QueryParam("cmd") String cmd) {
		Result<String> ret = shellService.execLocalShell(cmd);
		return buildResultVO(ret);
	}

	/**
	 * 获取 日志并展示
	 * 
	 * @param logpath
	 *            日志绝对路径
	 * @param lines
	 *            取日志行数，default 1000 MAX 5000
	 * @return
	 */
	@RequestMapping(value = "/getBuildLog")
	@ResponseBody
	public ResultVO<String> getBuildLog(@Context HttpServletRequest request,
			@QueryParam("applyId") Long applyId) {
		int lines = 2000;
		Result<Apply> ret = ValidUtils.validApply(applyId);
		if(!ret.isSuccess()){
			return ResultVO.fall(ret.getMsg());
		}
		Apply apply = ret.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project p = projectService.get(query).getValue();
		

		Result<String> retLog = shellService.execLocalShell("tail -n " + lines
				+ " " + p.getBuildLog());
		return buildResultVO(retLog);
	}

	@RequestMapping(value = "/getRemotelog")
	@ResponseBody
	public ResultVO<String> getRemotelog(@Context HttpServletRequest request,
			@QueryParam("applyId") Long applyId, @QueryParam("host") String host,
			@QueryParam("port") int port) {
		int lines = 2000;
		Result<Apply> ret = ValidUtils.validApply(applyId);
		if(!ret.isSuccess()){
			return ResultVO.fall(ret.getMsg());
		}
		Apply apply = ret.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project p = projectService.get(query).getValue();
		String user = p.getRemoteUser();
		
		
		String command = "tail -n " + lines + " " + p.getAppLog() ;
		Result<String> retLog = shellService.execRemoteShell(command, user, "", host, port);
//		Result<String> retLog = shellService.execLocalShell("ssh " + host + " -p "
//				+ port + " \"tail -n " + lines + " " + p.getAppLog() + "\"");
		return buildResultVO(retLog);
	}
	
	
	
	@RequestMapping(value = "/getlog")
	@ResponseBody
	public ResultVO<String> getlog(@Context HttpServletRequest request,
			@QueryParam("filename") String filename, @QueryParam("hostinfo") String hostinfo,
			@QueryParam("lines") int lines) {
		String[] hostArr = hostinfo.split(":");
        String user = hostArr[0];
        String host = hostArr[1];
        int port = Integer.parseInt(hostArr[2]);
        
        //判断是否text
        Result<String> retText = shellService.execRemoteShell("file "+filename, user, "", host, port);
        if(retText.isSuccess() && retText.getValue()!=null){
        	if(retText.getValue().endsWith("enpty")){
        		return ResultVO.fall("空文件");
        	}
			if (!(retText.getValue().indexOf(" text") > 0
					|| retText.getValue().endsWith("data") || retText
					.getValue().endsWith("data\n"))) {
				return ResultVO.fall("可能不是文本文件");
			}
        	
        }
        
		String command = "tail -n " + lines + " " + filename ;
		Result<String> retLog = shellService.execRemoteShell(command, user, "", host, port);
		return buildResultVO(retLog);
	}

	/**
	 * 0 初始化项目，创建项目的时候执行
	 * 
	 * @return
	 */
	@RequestMapping(value = "/init")
	@ResponseBody
	public ResultVO init(@Context HttpServletRequest request,
			@QueryParam("projectId") long projectId) {
		Result<Project> retT = ValidUtils.validProject(projectId);
		if(!retT.isSuccess()){
			return ResultVO.fall(retT.getMsg());
		}
		
		Project p = retT.getValue();
		String giturl = p.getGit();
		String appname = p.getAppName();
		String sourcePath = p.getSourcePath();
		File gitdir = new File(sourcePath);
		String binpath = ServerConfig.get("shell.path");
		Result<String> ret = new Result<String>();
		if (!gitdir.exists()) {
			gitdir.mkdirs();
			ret = shellService.execLocalShell(binpath + "init.sh " + appname
					+ " " + sourcePath + " " + giturl);
			LOG.error(ret.toString());
		} else {
			ret = shellService.execLocalShell(binpath + "git.sh " + appname
					+ " " + sourcePath + " master");
			LOG.error(ret.toString());
		}
		return buildResultVO(ret);
	}

	/**
	 * 0 初始化项目，创建项目的时候执行
	 * 
	 * @return
	 */
	@RequestMapping(value = "/checkProject")
	@ResponseBody
	public ResultVO<String> checkProject(@Context HttpServletRequest request,
			@QueryParam("projectId") long projectId) {
		Result<Project> retT = ValidUtils.validProject(projectId);
		if(!retT.isSuccess()){
			return ResultVO.fall(retT.getMsg());
		}
		Result<String> ret = new Result<String>(true, "检查通过");
		Project p = retT.getValue();
		String giturl = p.getGit();
		String appname = p.getAppName();
		String sourcePath = p.getSourcePath();
		File gitdir = new File(sourcePath);
		String binpath = ServerConfig.get("shell.path");
		String remoteUser = p.getRemoteUser();

		// 检查预发host
		String preHost = p.getPreHost();
		if (!StringUtils.isEmpty(preHost)) {
			if (preHost.indexOf("\n") > 0) {
				return ResultVO.<String> fall("预发机器配置错误");
			}
			if (preHost.split(":").length != 2) {
				return ResultVO.<String> fall("预发机器配置错误");
			}
		}
		// 检查线上host
		String onlineHost = p.getOnlineHost();
		if (StringUtils.isEmpty(onlineHost)) {
			return ResultVO.<String> fall("线上机器配置不能为空");
		}
		String[] hosts = StringUtils.split(onlineHost, "\n");
		for (String host : hosts) {
			String[] hp = StringUtils.split(host, ":");
			if (hp.length != 2) {
				return ResultVO.<String> fall("线上机器配置错误");
			}
		}
		
		//检查机器通路
		String[] tmpHost = preHost.split(":");
		Result<String> hostRet = shellService.execRemoteShell("ls", remoteUser, "", tmpHost[0], parseInt(tmpHost[1]));
		if(!hostRet.isSuccess()){
			return ResultVO.<String> fall("ssh error:"+preHost +":"+hostRet.getMsg());
		}
		for (String host : hosts) {
			String[] hp = StringUtils.split(host, ":");
			hostRet = shellService.execRemoteShell("ls", remoteUser, "", hp[0], parseInt(hp[1]));
			if(!hostRet.isSuccess()){
				return ResultVO.<String> fall("ssh error:"+host +":"+hostRet.getMsg());
			}
		}

		
		//检查发布机本地环境
		if (!gitdir.exists()) {
			gitdir.mkdirs();
			ret = shellService.execLocalShell(binpath + "init.sh " + appname
					+ " " + sourcePath + " " + giturl);
			if (!ret.isSuccess() || !ret.getValue().contains("success")) {
				return ResultVO.fall(ret.getStatus(), ret.getMsg());
			}
		} else {
			try {
				ret = shellService.execLocalShell(binpath + "init.sh " + appname
						+ " " + sourcePath + " " + giturl);
			} catch (Exception e) {
			}
			
			ret = shellService.execLocalShell(binpath + "git.sh " + appname
					+ " " + sourcePath + " " + giturl);
			LOG.error(ret.toString());
			if (!ret.isSuccess() || !ret.getValue().contains("success")) {
				return ResultVO.fall(ret.getStatus(), ret.getMsg());
			}
		}

		
		// TODO 不同类型项目检查
		if (ProjectConstants.PROJECT_TYPE_JAVA == p.getType()) {
			if(StringUtils.isEmpty(p.getPreBuild()) || StringUtils.isEmpty(p.getOnlineBuild())){
				return ResultVO.<String> fall("编译脚本配置错误");
			}
			
		} else if (ProjectConstants.PROJECT_TYPE_RESOURCE == p.getType()) {

		}else if (ProjectConstants.PROJECT_TYPE_GULP_RESOURCE == p.getType()) {
			if(StringUtils.isEmpty(p.getPreBuild()) || StringUtils.isEmpty(p.getOnlineBuild())){
				return ResultVO.<String> fall("编译脚本配置错误");
			}
		}

		ResultVO result = buildResultVO(ret);
		result.setMsg("检测通过");
		return result;
	}

	/**
	 * 1 git pull , git checkout barnch
	 * 
	 * @return
	 */
	@RequestMapping(value = "/git")
	@ResponseBody
	public ResultVO<String> git(@Context HttpServletRequest request,
			@QueryParam("applyId") long applyId) {

		Result<Apply> ret = ValidUtils.validApplyWithStatus(applyId);
		if(!ret.isSuccess()){
			return ResultVO.fall(ret.getMsg());
		}
		Apply apply = ret.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project p = projectService.get(query).getValue();

		String appname = p.getAppName();
		String branch = apply.getGitBranch();
		String sourcePath = p.getSourcePath();
		String binpath = ServerConfig.get("shell.path");
		Result<String> result = shellService.execLocalShell(binpath + "git.sh "
				+ appname + " " + sourcePath + " " + branch);
		if (result.isSuccess()) {
			updateProcess(applyId, ProjectConstants.APPLY_STATUS_GIT,
					result.getValue(), "更新代码");
		} else {
			updateProcess(applyId, ProjectConstants.APPLY_STATUS_START,
					result.getMsg(), "更新代码失败");
		}
		return buildResultVO(result);
	}

	/**
	 * 获取git所有版本信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/gitbranch")
	@ResponseBody
	public ResultVO gitbranch(@Context HttpServletRequest request,
			@QueryParam("projectId") long projectId) {

		Project p = null;
		ProjectQuery projectQuery = new ProjectQuery();
		projectQuery.setId(projectId);
		Result<Project> r_p = projectService.get(projectQuery);
		if (!r_p.isSuccess()) {
			return ResultVO.fall(r_p.getStatus(), r_p.getMsg());
		}
		p = r_p.getValue();
		if (p == null) {
			return ResultVO.fall("应用不存在!");
		}
		String appname = p.getAppName();
		String sourcePath = p.getSourcePath();
		String binpath = ServerConfig.get("shell.path");
		Result<String> result = shellService.execLocalShell(binpath
				+ "gitbranch.sh " + appname + " " + sourcePath);
		if (result.isSuccess()) {
			String retStr = result.getValue();
			String[] branchArr = retStr.split("\n");
			List<Map> rest = new ArrayList<Map>();
			Set<String> set = new HashSet<String>();
			for (String string : branchArr) {
				if (string.trim().startsWith("remotes/origin/HEAD")) {
					continue;
				}
				if (string.trim().startsWith("* master")) {
					continue;
				}
				String value = string.replace("remotes/origin/", "")
						.replace("*", "").trim();
				if(value.equals("")){
					continue;
				}
				set.add(value);
			}
			if(set.isEmpty() || set.size()==0){
				return ResultVO.fall("没有获取到分支信息，是否需要初始化下？");
			}
			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				Map<String,String> branchs = new HashMap<String,String>();
				branchs.put("text", string);
				branchs.put("value", string);
				rest.add(branchs);
			}
			
			// branchs.add("master");
			Result<List> res = new Result<List>();
			res.setSuccess(true).setValue(rest);
			return buildResultVO(res);
		}

		return buildResultVO(result);
	}
	
	
	
	/**
	 * 获取git最近提交的10个版本
	 * 
	 * @return
	 */
	@RequestMapping(value = "/gitlog")
	@ResponseBody
	public ResultVO gitlog(@Context HttpServletRequest request,
			@QueryParam("projectId") long projectId) {
		Project p = null;
		ProjectQuery projectQuery = new ProjectQuery();
		projectQuery.setId(projectId);
		Result<Project> r_p = projectService.get(projectQuery);
		if (!r_p.isSuccess()) {
			return ResultVO.fall(r_p.getStatus(), r_p.getMsg());
		}
		p = r_p.getValue();
		if (p == null) {
			return ResultVO.fall("应用不存在!");
		}
		String appname = p.getAppName();
		String sourcePath = p.getSourcePath();
		String binpath = ServerConfig.get("shell.path");
		
		Result<String> result = shellService.execLocalShell(binpath
				+ "gitlog.sh " + appname + " " + sourcePath);
		if (result.isSuccess()) {
			String retStr = result.getValue();
			String[] logArr = retStr.split("\n");
			List<Map> rest = new ArrayList<Map>();
			for (String string : logArr) {
				Map<String,String> branchs = new HashMap<String,String>();
				branchs.put("text", string);
				branchs.put("value", string);
				if(string.equals("")){
					continue;
				}
				rest.add(branchs);
			}
			if(rest.isEmpty() || rest.size()==0){
				return ResultVO.fall("没有获取到分支信息，是否需要初始化下？");
			}
			
			Result<List> res = new Result<List>();
			res.setSuccess(true).setValue(rest);
			return buildResultVO(res);
		}
		return buildResultVO(result);
	}
	
	
	
	/**
	 * git 更新到某个commit版本
	 * 
	 * @return
	 */
	@RequestMapping(value = "/git2version")
	@ResponseBody
	public ResultVO git2version(@Context HttpServletRequest request,
			@QueryParam("gitVersion") String gitVersion,
			@QueryParam("appname") String appname,
			@QueryParam("sourcePath") String sourcePath,
			@QueryParam("version") String version) {
		String binpath = ServerConfig.get("shell.path");
		Result<String> result = shellService.execLocalShell(binpath
				+ "git2version.sh " + appname + " " + sourcePath + " " + version);
		return buildResultVO(result);
	}
	
	
	
//	/**
//	 * 2 build war
//	 * 
//	 * @return
//	 */
//	@RequestMapping(value = "/buildtmp")
//	@ResponseBody
//	public ResultVO<String> buildtmp(@Context HttpServletRequest request,
//			@QueryParam("applyId") long applyId) {
//		Result<Apply> ret = ValidUtils.validApply(applyId);
//		if(!ret.isSuccess()){
//			return ResultVO.fall(ret.getMsg());
//		}
//		Apply apply = ret.getValue();
//		ProjectQuery query = new ProjectQuery();
//		query.setId(apply.getProjectId());
//		Project p = projectService.get(query).getValue();
//		return null;
//	}
	
	

	/**
	 * 2 build war
	 * 
	 * @return
	 */
	@RequestMapping(value = "/build")
	@ResponseBody
	public ResultVO<String> build(@Context HttpServletRequest request,
			@QueryParam("applyId") long applyId,@QueryParam("online") boolean online) {

		Result<Apply> ret = ValidUtils.validApplyWithStatus(applyId);
		if(!ret.isSuccess()){
			return ResultVO.fall(ret.getMsg());
		}
		Apply apply = ret.getValue();
		if(online){ //
			if(apply.getStatus()<ProjectConstants.APPLY_STATUS_GIT){
				return ResultVO.fall("先执行预发操作");
			}
		}
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project p = projectService.get(query).getValue();
		String preBuildScript = p.getPreBuild();
		String onlineBuildScript = p.getOnlineBuild();
		preBuildScript += " -l " + p.getBuildLog();
		onlineBuildScript += " -l " + p.getBuildLog();
		boolean done = false;
		Result<String> result = null;
		//remove war 
		File war = new File(p.getFileName());
		if(war !=null && war.isFile()){
			boolean t = war.delete();
		}
		if(online && !StringUtils.isEmpty(onlineBuildScript)){
			result = shellService.execLocalShell(onlineBuildScript);
		}else if(!online && !StringUtils.isEmpty(preBuildScript)){
			result = shellService.execLocalShell(preBuildScript);
		}
		war = new File(p.getFileName());
		if(war !=null && war.isFile()){
			done = true;
		}
		
		if(!done){		
			//TODO 需要去掉  for entpay not support mvn -f
			String env = (online?" online ":" pre " );
			if(onlineBuildScript.indexOf("vpc") != -1){
				env = " vpc ";
			}
			///////////////
			String cmd = ServerConfig.get("shell.path") + "/build.sh "
					+ p.getAppName() + " " + p.getSourcePath() + " " + p.getBuildLog()
					+ env + p.getFileName();
			result = shellService.execLocalShell(cmd);
		}
		if (result.isSuccess()) {
			
			//buildLog 中含有 SUCCESS  不含 FAILURE 才成功
			boolean secondJudge = false;
			try {
				File tmpf = new File(p.getBuildLog());
				if(tmpf.exists()){
					String buildLog = FileUtil.readAsString(tmpf);
					if(buildLog.indexOf("SUCCESS") !=-1 && buildLog.indexOf("FAILURE") ==-1){
						secondJudge = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			if (result.getValue()!=null && secondJudge) {		
				if(online){
					updateProcess(applyId, ProjectConstants.APPLY_STATUS_BUILD_ONLINE_OK,
							result.getValue(), "线上编译成功");
				}else{
					updateProcess(applyId, ProjectConstants.APPLY_STATUS_BUILD_OK,
						result.getValue(), "预发编译成功");
				}
			} else {
				result.setMsg("编译错误").setSuccess(false);
				if(online){
					updateProcess(applyId,
							ProjectConstants.APPLY_STATUS_BUILD_FAILED,
							result.getValue(), "预发编译错误");
				}else{
					updateProcess(applyId,
							ProjectConstants.APPLY_STATUS_BUILD_ONLINE_FAILED,
							result.getValue(), "线上编译错误");
				}
			}
		} else {
			updateProcess(applyId, ProjectConstants.APPLY_STATUS_BUILD_FAILED,
					result.getMsg(), "编译错误");
		}
		return buildResultVO(result);
	}

	/**
	 * 4 sync war  ||  sync static file 
	 * 
	 * @return
	 */
	@RequestMapping(value = "/syncfile")
	@ResponseBody
	public ResultVO<String> syncfile(@Context HttpServletRequest request,
			@QueryParam("applyId") long applyId,
			@QueryParam("online") boolean online, @QueryParam("idx") int idx) {

		Result<Apply> retA = ValidUtils.validApplyWithStatus(applyId);
		if(!retA.isSuccess()){
			return ResultVO.fall(retA.getMsg());
		}
		Apply apply = retA.getValue();
		if(online && apply.getFlag()!=ProjectConstants.APPLY_FLAG_NORMAL ){
			return ResultVO.fall("发布单审核状态错误!");
		}
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project p = projectService.get(query).getValue();
		String remoteUser = p.getRemoteUser();
		
		String cmd = null;
		
		/****  静态资源目录初始化  ***/
		if(p.getType()==ProjectConstants.PROJECT_TYPE_RESOURCE){
			String hostinfo = p.getOnlineHost();
			if(!online){
				hostinfo = p.getPreHost();
			}
			String[] lines = hostinfo.split("\r\n");
			String host = lines[idx];
			String[] hp = host.split(":");
			
			//初始化目录
			String rootDir  = p.getTargetPath();
			String initcommand = "mkdir -p "+rootDir;
			shellService.execRemoteShell(initcommand, p.getRemoteUser(), "", hp[0], parseInt(hp[1]));
		}
		
		/****  静态资源分目录发布  ***/
		if(p.getType()==ProjectConstants.PROJECT_TYPE_RESOURCE && !StringUtils.isEmpty(apply.getFileList())){
			ResultVO<String> r = syncStaticFileList(p, apply, online, idx);
			if(r.isSuccess()){
				if(online){
					updateProcess(applyId,
							apply.getStatus(),
							r.getData(), "文件同步目标机器("+p.getOnlineHost().split(":")[idx]+")成功");
					apply.setIdx(idx + 1);
				}else{
					updateProcess(applyId,
							ProjectConstants.APPLY_STATUS_SYNC_PRE_OK,
							r.getData(), "文件同步目标机器("+p.getPreHost()+")成功");
					apply.setIdx(0);
				}
			}else{
				
			}
			
			applyService.update(apply);
			return r;
		}
		
		try {
			if (online ) {
				
				String onlinehost = p.getOnlineHost();
				String[] lines = onlinehost.split("\r\n");
				String host = lines[idx];
				String[] hp = host.split(":");
				
				/*******    java类型第一次发线上前备份war包 start   *****/
				boolean ok = false;
				String backpath = ServerConfig.get("backup.war.path") + p.getAppName() +"/";
				String backwar = backpath+p.getId()+"-"+apply.getId()+".war";
				if(p.getType()==ProjectConstants.PROJECT_TYPE_JAVA){
					File dir = new File(backpath);
					File war = new File(backwar);
					if(!dir.exists()){
						dir.mkdirs();
						ok = true;
					}
					if(!war.exists()){
						ok = true;
					}
				}
				if(ok){
					String backupCmd = ServerConfig.get("shell.path") + "/backup.sh "
							+ p.getAppName() + " " + backwar + " "
							+ p.getTargetPath()+"/"+ parseWarName(p.getFileName()) + " " + remoteUser + " " + hp[0]+ " " + parseInt(hp[1]);
					Result<String> result = shellService.execLocalShell(backupCmd);
					if(!result.isSuccess()){
						return ResultVO.fall("备份war失败："+result.getMsg());
					}
				}
				/*******    java类型第一次发线上前备份war包 end   *****/
				
				cmd = ServerConfig.get("shell.path") + "/syncfile.sh "
						+ p.getAppName() + " " + p.getFileName() + " "
						+ p.getTargetPath() + " " + remoteUser + " " + hp[0] + " " + parseInt(hp[1]);
			} else{
				String prehost = p.getPreHost();
				if (prehost != null) {
					String[] hp = prehost.split(":");
					cmd = ServerConfig.get("shell.path") + "/syncfile.sh "
							+ p.getAppName() + " " + p.getFileName() + " "
							+ p.getTargetPath() + " " + remoteUser + " " + hp[0]+ " " + parseInt(hp[1]);
				}
			}
		} catch (Exception e) {
			Result<String> result = (new Result<String>()).setSuccess(false)
					.setMsg("project host error!");
			return buildResultVO(result);
		}

		// syncfile.sh appname warname target_path host port
		Result<String> result = shellService.execLocalShell(cmd);
		if (result.isSuccess()) {
			if (result.getValue().contains("success")) {
				
				if(online){
					updateProcess(applyId,
							apply.getStatus(),
							result.getValue(), "文件同步目标机器("+p.getOnlineHost().split(":")[idx]+")成功");
					apply.setIdx(idx + 1);
				}else{
					updateProcess(applyId,
							ProjectConstants.APPLY_STATUS_SYNC_PRE_OK,
							result.getValue(), "文件同步目标机器("+p.getPreHost()+")成功");
					apply.setIdx(0);
				}
				applyService.update(apply);
			} else {
				result.setMsg("文件同步目标机器("+idx+")失败\n"+result.getValue()).setSuccess(false);
				updateProcess(
						applyId,
						online ? apply.getStatus()
								: ProjectConstants.APPLY_STATUS_SYNC_PRE_FAILED,
						result.getValue(), "文件同步目标机器("+idx+")失败");
			}
		} else {
			updateProcess(applyId,
					online ? apply.getStatus()
							: ProjectConstants.APPLY_STATUS_SYNC_PRE_FAILED,
					result.getMsg(), "文件同步目标机器("+idx+")失败");
		}
		return buildResultVO(result);
	}
	
	
	/**
	 * 分文件发布的静态资源项目
	 * @param p
	 * @param a
	 * @return
	 */
	private ResultVO<String> syncStaticFileList(Project p , Apply a,boolean online,int idx){
		
		
		String hostinfo = p.getOnlineHost();
		if(!online){
			hostinfo = p.getPreHost();
		}
		String[] lines = hostinfo.split("\r\n");
		String host = lines[idx];
		String[] hp = host.split(":");
		
		
		
		String fileStr = a.getFileList();
		String[] files = StringUtils.split(fileStr,",");
		StringBuilder sb = new StringBuilder();
		for (String file : files) {
			String targetFile = p.getTargetPath()+"/"+file;
			int lastIdx = targetFile.lastIndexOf("/");
			String targetDir = targetFile.substring(0, lastIdx);
			String cmd = ServerConfig.get("shell.path") + "/syncfile.sh "
					+ p.getAppName() + " " + p.getFileName()+"/"+file + " "
					+ targetDir + " " + p.getRemoteUser() + " " + hp[0]+ " " + parseInt(hp[1]);
			if(lastIdx>0){
				targetDir = targetDir.substring(0, lastIdx);
				String command = "if [ ! -d \""+targetDir+"\" ]; then mkdir -p "+targetDir+"; fi";
				shellService.execRemoteShell(command, p.getRemoteUser(), "", hp[0], parseInt(hp[1]));
			}
			Result<String> result = shellService.execLocalShell(cmd);
			if(result.isSuccess()){
				sb.append(result.getValue()).append("\n");
			}else{
				sb.append(result.getMsg()).append("\n");
			}
		}
		return ResultVO.success(sb.toString());
	}

	/**
	 * 同步文件前的预操作，比如停止服务
	 * 
	 * @param request
	 * @param online
	 * @return
	 */
	@RequestMapping(value = "/preSync")
	@ResponseBody
	public ResultVO<String> preSync(@Context HttpServletRequest request,
			@QueryParam("applyId") long applyId,
			@QueryParam("online") boolean online, @QueryParam("idx") int idx) {
		Result<Apply> retA = ValidUtils.validApplyWithStatus(applyId);
		if(!retA.isSuccess()){
			return ResultVO.fall(retA.getMsg());
		}
		Apply apply = retA.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project project = projectService.get(query).getValue();
		
		if(project.getBeforeSync()==null || project.getBeforeSync().trim().equals("")){
			return ResultVO.success("无脚本需要执行");
		}
		String[] cmds = StringUtils.split(project.getBeforeSync(), "\r\n");
		String remoteUser = project.getRemoteUser();
		String cmd = "";
		Result<String> result = new Result<String>(false, "");
		StringBuilder sb = new StringBuilder();
		try {
			if (online) {
				String onlinehost = project.getOnlineHost();
				String[] lines = onlinehost.split("\r\n");
				String host = lines[idx];
				String[] hp = host.split(":");
				for (String command : cmds) {
					if(!StringUtils.isEmpty(command.trim())){
						String tmp = ServerConfig.get("shell.path") + "/execremote.sh "
								+ project.getAppName() + " " + remoteUser + " " + hp[0] + " " + parseInt(hp[1]) + " "
								+ command  ;
						result = shellService.execLocalShell(tmp);
						sb.append(result.getValue());
						if(!result.isSuccess()){
							break;
						}
					}
				}
			} else {
				String prehost = project.getPreHost();
				if (prehost != null) {
					String[] hp = prehost.split(":");
					for (String command : cmds) {
						if(!StringUtils.isEmpty(command.trim())){
							String tmp = ServerConfig.get("shell.path") + "/execremote.sh "
									+ project.getAppName() + " " + remoteUser + " " + hp[0] + " " + parseInt(hp[1]) + " "
								+ command  ;
							result = shellService.execLocalShell(tmp);
							sb.append(result.getValue());
							if(!result.isSuccess()){
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Result<String> ret = (new Result<String>()).setSuccess(false)
					.setMsg("project host error!");
			return buildResultVO(result);
		}
		
		if(online){
			updateProcess(applyId,
					ProjectConstants.APPLY_STATUS_SYNC_ONLINE_ING,
					result.getMsg(), "执行同步前脚本：目标机器("+project.getOnlineHost().split(":")[idx]+")! Execute shell:" + project.getBeforeSync());
		}else{
			updateProcess(applyId,
					apply.getStatus(),
					result.getMsg(), "执行同步前脚本：预发目标机器("+project.getPreHost()+")! Execute shell:" + project.getBeforeSync());
		}
		return buildResultVO(result.setValue(sb.toString()));
	}

	/**
	 * 同步文件后的补充操作 重启服务，健康检查等
	 * 
	 * @param request
	 * @param online
	 * @return
	 */
	@RequestMapping(value = "/afterSync")
	@ResponseBody
	public ResultVO<String> afterSync(@Context HttpServletRequest request,
			@QueryParam("applyId") long applyId,
			@QueryParam("online") boolean online, @QueryParam("idx") int idx) {
		Result<Apply> retA = ValidUtils.validApplyWithStatus(applyId);
		if(!retA.isSuccess()){
			return ResultVO.fall(retA.getMsg());
		}
		Apply apply = retA.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project project = projectService.get(query).getValue();
		if(project.getAfterSync()==null || project.getAfterSync().trim().equals("")){
			return ResultVO.success("无脚本需要执行");
		}
		String[] cmds = StringUtils.split(project.getAfterSync(), "\r\n");

		String cmdpath = ServerConfig.get("shell.path");
		String cmd = "";
		String remoteUser = project.getRemoteUser();
		String onlinehost = project.getOnlineHost();
		String[] lines = onlinehost.split("\r\n");
		String host = lines[idx];
		
		StringBuilder sb = new StringBuilder();
		Result<String> result = new Result<String>(false, "");
		try {
			if (online) {
				String[] hp = host.split(":");
				for (String command : cmds) {
					if(!StringUtils.isEmpty(command.trim())){
						String tmp = cmdpath+ "/execremote.sh "
								+ project.getAppName() + " "+remoteUser + "  " + hp[0] + " " + parseInt(hp[1]) + " "
								+ command ;
						result = shellService.execLocalShell(tmp);
						sb.append(result.getValue());
						if(!result.isSuccess()){
							break;
						}
					}
				}
			} else {
				String prehost = project.getPreHost();
				if (prehost != null) {
					String[] hp = prehost.split(":");
					for (String command : cmds) {
						if(!StringUtils.isEmpty(command.trim())){
							String tmp = cmdpath + "/execremote.sh "
									+ project.getAppName() + " "+remoteUser + " " + hp[0] + " " + parseInt(hp[1]) + " "
								+ command + " ; ";
							result = shellService.execLocalShell(tmp);
							sb.append(result.getValue());
							if(!result.isSuccess()){
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Result<String> ret = (new Result<String>()).setSuccess(false)
					.setMsg("project host error!");
			return buildResultVO(ret);
		}
		
		if (result.isSuccess()) {
			if (result.getValue().contains("success")) {
				if(online){
					if(idx==lines.length-1){
						updateProcess(applyId,ProjectConstants.APPLY_STATUS_PUB_ONLINE_OK, result.getValue(), "执行同步后脚本成功，目标机("+host+")");
					}else{
						updateProcess(applyId,apply.getStatus(), result.getValue(), "执行同步后脚本成功，目标机("+host+")");
					}
					apply.setIdx(idx + 1);
					applyService.update(apply);
				}else{
					updateProcess(applyId,ProjectConstants.APPLY_STATUS_RESTART_PRE_OK, result.getValue(), "执行同步后脚本 预发机成功");
				}
			} else {
				result.setMsg("执行同步后脚本失败\n"+result.getValue()).setSuccess(false);
				if(online){
					if(idx==lines.length-1){
						updateProcess(applyId,apply.getStatus(), result.getValue(), "执行同步后脚本失败，目标机("+host+")");
					}else{
						updateProcess(applyId,apply.getStatus(), result.getValue(), "执行同步后脚本失败，目标机("+host+")");
					}
					apply.setIdx(idx + 1);
					applyService.update(apply);
				}else{
					updateProcess(applyId,ProjectConstants.APPLY_STATUS_RESTART_PRE_FAILED, result.getValue(), "预发机执行同步后脚本失败");
				}
			}
		} else {
			result.setMsg("执行同步后脚本失败").setSuccess(false);
			if(online){
				if(idx==lines.length-1){
					updateProcess(applyId,apply.getStatus(), result.getValue(), "执行同步后脚本失败，目标机("+host+")");
				}else{
					updateProcess(applyId,apply.getStatus(), result.getValue(), "执行同步后脚本失败，目标机("+host+")");
				}
				apply.setIdx(idx + 1);
				applyService.update(apply);
			}else{
				updateProcess(applyId,ProjectConstants.APPLY_STATUS_RESTART_PRE_FAILED, result.getValue(), "预发机执行同步后脚本失败");
			}
		}
		
		return buildResultVO(result.setValue(sb.toString()));
	}
	
	

	/**
	 * 发布完成后打tag
	 * 
	 * @param request
	 * @param online
	 * @return
	 */
	@RequestMapping(value = "/gitTag")
	@ResponseBody
	public ResultVO<String> gitTag(@Context HttpServletRequest request,
			@QueryParam("applyId") long applyId) {
		Result<Apply> retA = ValidUtils.validApplyWithStatus(applyId);
		if(!retA.isSuccess()){
			return ResultVO.fall(retA.getMsg());
		}
		Apply apply = retA.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project project = projectService.get(query).getValue();
		
		String cmdpath = ServerConfig.get("shell.path");
		String tagName = UserUtil.getUserName(request)+apply.getTitle();
		String cmd =  cmdpath+ "/gittag.sh "
				+ project.getAppName() + " " + project.getSourcePath() + " " +tagName ;
		
		Result<String> result = shellService.execLocalShell(cmd);
		return buildResultVO(result);
	}
	
	
	
	/**
	 * 回滚代码
	 * 
	 * @param request
	 * @param online
	 * @return
	 */
	@RequestMapping(value = "/rollback")
	@ResponseBody
	public ResultVO rollback(@Context HttpServletRequest request,
			@QueryParam("applyId") Long applyId) {
		Result<Apply> retA = ValidUtils.validApply(applyId);
		if(!retA.isSuccess()){
			return ResultVO.fall(retA.getMsg());
		}
		Apply apply = retA.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project p = projectService.get(query).getValue();
		String remoteUser = p.getRemoteUser();
			
		String onlinehost = p.getOnlineHost();
		String[] lines = onlinehost.split("\r\n");
		for (String host : lines) {
			String[] hp = host.split(":");
			/*******    java类型回滚备份的war包 start   *****/
			String backpath = ServerConfig.get("backup.war.path") + p.getAppName() +"/";
			String backwar = backpath+p.getId()+"-"+apply.getId()+".war";
			File war = new File(backwar);
			if(!war.exists()){
				return ResultVO.<String> fall(Errors.E_NOTEXIST);
			}
			String rollbackCmd = ServerConfig.get("shell.path") + "/rollback.sh "
					+ p.getAppName() + " " + backwar + " "
					+ p.getTargetPath()+"/"+ parseWarName(p.getFileName()) + " " + remoteUser + " " + hp[0]+ " " + parseInt(hp[1]);
			Result<String> result = shellService.execLocalShell(rollbackCmd);
			if(!result.isSuccess()){
				return ResultVO.fall( host+"回滚失败，"+result.getMsg());
			}
			updateProcess(
					applyId,
					ProjectConstants.APPLY_STATUS_ROLLBACK,
					result.getValue(), "回滚完成");
			/*******    java类型回滚备份的war包 end   *****/
		}
		return ResultVO.success("ok");
	}

	
	/**
	 * 不同类型的发布单 脚本执行顺序
	 * 
	 * @param request
	 * @param online
	 * @return
	 */
	@RequestMapping(value = "/scripts")
	@ResponseBody
	public ResultVO scripts(@Context HttpServletRequest request,@QueryParam("applyId") Long applyId) {
		
		ApplyQuery applyQuery = new ApplyQuery();
		applyQuery.setId(applyId);
		Result<Apply> r_a = applyService.get(applyQuery);
		
		if(!r_a.isSuccess() || r_a.getValue()==null){
			ResultVO.fall("发布单不存在");
		}
		Apply apply = r_a.getValue();
		
		ProjectQuery projectQuery = new ProjectQuery();
		projectQuery.setId(apply.getProjectId());
		Result<Project> r_p = projectService.get(projectQuery);
		if(!r_p.isSuccess() || r_p.getValue()==null){
			ResultVO.fall("项目不存在");
		}
		Project p = r_p.getValue();
		int type = p.getType();
		String preHost = p.getPreHost();
		String onlineHost = p.getOnlineHost();
		
		
		Map<String,Object> pre = new HashMap<String,Object>();
		pre.put("host", preHost);
		pre.put("check", getStatus(apply,ProjectConstants.APPLY_STATUS_START));
		pre.put("update", getStatus(apply,ProjectConstants.APPLY_STATUS_START));
		if(type==ProjectConstants.PROJECT_TYPE_JAVA)pre.put("build", getStatus(apply,ProjectConstants.APPLY_STATUS_GIT));
		if(!StringUtils.isEmpty(preHost)){
			if(type==ProjectConstants.PROJECT_TYPE_JAVA)pre.put("beforeSync", getStatus(apply,ProjectConstants.APPLY_STATUS_BUILD_FAILED));
			pre.put("syncFile", getStatus(apply,ProjectConstants.APPLY_STATUS_BUILD_FAILED));
			if(type==ProjectConstants.PROJECT_TYPE_JAVA)pre.put("afterSync", getStatus(apply,ProjectConstants.APPLY_STATUS_SYNC_PRE_FAILED));
		}
		
		Map<String,Object> online = new HashMap<String,Object>();
		List<Map> scripts = new ArrayList<Map>();
		String[] hosts = StringUtils.split(onlineHost, "\n");
		int idx = 0;
		for (String host : hosts) {
			Map<String,Object> hostMap = new HashMap<String,Object>();
			hostMap.put("host", host);
			if(type==ProjectConstants.PROJECT_TYPE_JAVA)hostMap.put("build", getStatus(apply,ProjectConstants.APPLY_STATUS_SYNC_ONLINE_READY));
			if(type==ProjectConstants.PROJECT_TYPE_JAVA)hostMap.put("beforeSync", getStatus(idx,apply,ProjectConstants.APPLY_STATUS_BUILD_ONLINE_FAILED));
			hostMap.put("syncFile", getStatus(idx,apply,ProjectConstants.APPLY_STATUS_BUILD_ONLINE_FAILED));
			if(type==ProjectConstants.PROJECT_TYPE_JAVA)hostMap.put("afterSync", getStatus(idx,apply,ProjectConstants.APPLY_STATUS_BUILD_ONLINE_FAILED));
			idx ++;
			scripts.add(hostMap);
		}
		online.put("hosts", scripts);
		

		Map result = new HashMap();
		result.put("pre", pre);
		result.put("online", online);
		result.put("tag", getStatus(apply,ProjectConstants.APPLY_STATUS_PUB_ONLINE_OK));
		
		return buildResultVO(new Result().setSuccess(true).setValue(result));
	}
	
	
	@RequestMapping(value = "/listDir")
	@ResponseBody
	public ResultVO listDir(@Context HttpServletRequest request,
			@QueryParam("dir") String dir,@QueryParam("hostinfo") String hostinfo) {
		
		String[] hostArr = hostinfo.split(":");
        String user = hostArr[0];
        String host = hostArr[1];
        int port = Integer.parseInt(hostArr[2]);
        String commandDir = "ls -l "+dir+" | grep ^[d] | awk '{print $9}'";
		Result<String> result = shellService.execRemoteShell(commandDir, user, "", host, port);
		
		String commandFile = "ls -l "+dir+" | grep ^[^d] | awk '{print $9}'";
		Result<String> result2 = shellService.execRemoteShell(commandFile, user, "", host, port);
		List<String> dirs = new ArrayList<String>();
		if(result.isSuccess() && !StringUtils.isEmpty(result.getValue())){
			String[] tmp = StringUtils.split(result.getValue(), "\n");
			for (String string : tmp) {
				dirs.add(string);
			}
		}
		
		List<String> files = new ArrayList<String>();
		if(result2.isSuccess() && !StringUtils.isEmpty(result2.getValue())){
			String[] tmp = StringUtils.split(result2.getValue(), "\n");
			for (String string : tmp) {
				files.add(string);
			}
		}
		
		LinkedList<Map> resultFile = new LinkedList<Map>();
		for (String file : files) {
			Map map = new HashMap();
			map.put("name", file);
			map.put("type", "file");
			resultFile.add(map);
		}
		
		for (String _dir : dirs) {
			Map map = new HashMap();
			map.put("name", _dir);
			map.put("type", "dir");
			resultFile.add(map);
		}
		
		return ResultVO.success(resultFile);
		//ls -l | grep ^[d] | awk '{print $9}'
		
		//ls -l | grep ^[^d] | awk '{print $9}'
	}
	
	
	/**
	 * 健康检查
	 * 
	 * @param request
	 * @param online
	 * @return
	 */
	@RequestMapping(value = "/checkHealth")
	@ResponseBody
	public ResultVO<String> checkHealth(@Context HttpServletRequest request,
			@QueryParam("applyId") long applyId, @QueryParam("idx") int idx) {
		Result<Apply> retA = ValidUtils.validApplyWithStatus(applyId);
		if(!retA.isSuccess()){
			return ResultVO.fall(retA.getMsg());
		}
		Apply apply = retA.getValue();
		ProjectQuery query = new ProjectQuery();
		query.setId(apply.getProjectId());
		Project project = projectService.get(query).getValue();
		if(project.getCheckUri()==null || project.getCheckUri().trim().equals("")){
			return ResultVO.fall("没加健康检查uri");
		}
		
		String onlinehost = project.getOnlineHost();
		String[] lines = onlinehost.split("\r\n");
		String host = lines[idx];
		
		try {
			String[] hp = host.split(":");
			String checkhost = hp[0];
			String checkUrl = "http://"+checkhost+project.getCheckUri();
			String ret = HttpUtils.executeGet(checkUrl);
			if(ret!=null && ret.trim().toUpperCase().contains("SUCCESS")){
				return ResultVO.success("健康检查通过");
			}else{
				return ResultVO.fall(ret);
			}
			
		} catch (Exception e) {
			Result<String> ret = (new Result<String>()).setSuccess(false)
					.setMsg("project host error!");
			return buildResultVO(ret);
		}
	}
	
	
	private boolean getStatus(Apply a , int s){
		return getStatus(0, a, s);
	}
	private boolean getStatus(Integer idx,Apply a , int s){
		if(s==ProjectConstants.APPLY_STATUS_START && a.getStatus()==s){	
			return false;
		}
		if(s < ProjectConstants.APPLY_STATUS_BUILD_ONLINE_FAILED || s >= ProjectConstants.APPLY_STATUS_PUB_ONLINE_OK){	
			return a.getStatus()>s;
		}
		
		//online
		if(a.getStatus() >= ProjectConstants.APPLY_STATUS_BUILD_ONLINE_FAILED && a.getStatus() < ProjectConstants.APPLY_STATUS_PUB_ONLINE_OK){
			if(a.getIdx()>idx){
				return true;
			}else{
				return false;
			}
		}
		return a.getStatus()>s;
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
	
	private String parseWarName(String abpathFileName){
		if(StringUtils.isEmpty(abpathFileName)){
			return null;
		}
		int idx = abpathFileName.lastIndexOf("/");
		if(idx>0){
			return abpathFileName.substring(idx, abpathFileName.length());
		}
		return abpathFileName;
	}
	
	private Integer parseInt(String port){
		try {
			return Integer.parseInt(port.replace("\r", "").trim());
		} catch (Exception e) {
			return 9092;
		}
	}
	
	
	
}
