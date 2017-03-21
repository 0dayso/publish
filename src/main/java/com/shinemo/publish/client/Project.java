
package com.shinemo.publish.client;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.shinemo.publish.common.BaseDO;

public class Project extends BaseDO {
	private static final long serialVersionUID = 9147091261506104916L;
	private Long id;
	private String title;
	private String name;
	private String descr;
	private String git;
	private String preHost;
	/**
	* 机器列表：端口
	*/
	private String onlineHost;
	/**
	* 开发负责人
	*/
	private Long ownerId;
	/**
	* 测试负责人
	*/
	private Long testId;
	/**
	* 0:java   1:静态资源
	*/
	private Integer type;
	/**
	* 源代码路径
	*/
	private String sourcePath;
	/**
	* 目标机器文件位置
	*/
	private String targetPath;
	private Date gmtCreate;
	private Date gmtModified;
	/**
	* 要同步的文件，可以是目录
	*/
	private String fileName;
	private String buildLog;
	private String appLog;
	
	/**
	 * 同步文件前执行的脚本
	 */
	private String beforeSync;
	/**
	 * 同步文件后执行的脚本
	 */
	private String afterSync;
	
	/**
	 * 目标机器登录用户
	 */
	private String remoteUser;
	
	/**
	 * 是否需要审批才能发布
	 */
	private Integer needApproval;
	
	private String preBuild;
	private String onlineBuild;
	
	private Integer env; //0、优办；1、彩云；2、麻绳；3、小沃
	
	private String checkUri;	//健康检查
	
	private Integer stat;	// 状态  0正常
	
	private Integer debugPort;	//debug 端口
	
	private List<String> debugHost;	// debug host
	
	
	public List<String> getDebugHost() {
		return debugHost;
	}

	public void setDebugHost(List<String> debugHost) {
		this.debugHost = debugHost;
	}

	public Integer getDebugPort() {
		return debugPort;
	}

	public void setDebugPort(Integer debugPort) {
		this.debugPort = debugPort;
	}

	public Integer getStat() {
		return stat;
	}

	public void setStat(Integer stat) {
		this.stat = stat;
	}

	public String getCheckUri() {
		return checkUri;
	}

	public void setCheckUri(String checkUri) {
		this.checkUri = checkUri;
	}

	public Integer getEnv() {
		return env;
	}

	public void setEnv(Integer env) {
		this.env = env;
	}

	public String getPreBuild() {
		return preBuild;
	}

	public void setPreBuild(String preBuild) {
		this.preBuild = preBuild;
	}

	public String getOnlineBuild() {
		return onlineBuild;
	}

	public void setOnlineBuild(String onlineBuild) {
		this.onlineBuild = onlineBuild;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public String getAppName() {
		String git = getGit();
		if(git==null){
			throw new RuntimeException("git is null");
		}
		int start = git.lastIndexOf("/")+1;
		int end = git.lastIndexOf(".");
		if(start > end){
			throw new RuntimeException("git config is invalid");
		}
		return git.substring(start, end);
	}
	

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getGit() {
		return git;
	}

	public void setGit(String git) {
		this.git = git;
	}

	public String getPreHost() {
		return preHost;
	}

	public void setPreHost(String preHost) {
		this.preHost = preHost;
	}

	public String getOnlineHost() {
		return onlineHost;
	}

	public void setOnlineHost(String onlineHost) {
		this.onlineHost = onlineHost;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getBuildLog() {
		return buildLog;
	}

	public void setBuildLog(String buildLog) {
		this.buildLog = buildLog;
	}

	public String getAppLog() {
		return appLog;
	}

	public void setAppLog(String appLog) {
		this.appLog = appLog;
	}

	public String getBeforeSync() {
		return beforeSync;
	}

	public void setBeforeSync(String beforeSync) {
		this.beforeSync = beforeSync;
	}

	public String getAfterSync() {
		return afterSync;
	}

	public void setAfterSync(String afterSync) {
		this.afterSync = afterSync;
	}

	public String getRemoteUser() {
		if(StringUtils.isEmpty(remoteUser)){
			return "shinemo-safe";
		}
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public Integer getNeedApproval() {
		return needApproval;
	}

	public void setNeedApproval(Integer needApproval) {
		this.needApproval = needApproval;
	}
	
}
