
package com.shinemo.publish.client;

import java.util.Date;

import com.shinemo.publish.common.BaseDO;


public class Apply extends BaseDO {
	
	private static final long serialVersionUID = -4750023611926199920L;
	private Long id;
	private String title;
	private String gitBranch;
	private Long userId;
	private Integer flag;
	private Integer status;
	private Date gmtCreate;
	private Date gmtModified;
	private Long projectId;
	private Integer idx;
	private String gitVersion;
	private String fileList;
	/**
	* 执行日志
	*/
	private String log;

	
	public String getFileList() {
		return fileList;
	}

	public void setFileList(String fileList) {
		this.fileList = fileList;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGitBranch() {
		return gitBranch;
	}

	public void setGitBranch(String gitBranch) {
		this.gitBranch = gitBranch;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public String getGitVersion() {
		return gitVersion;
	}

	public void setGitVersion(String gitVersion) {
		this.gitVersion = gitVersion;
	}

	
}
