package com.shinemo.publish.client;

import java.util.Date;

import com.shinemo.publish.common.QueryBase;

public class ApplyQuery extends QueryBase {
	private static final long serialVersionUID = -1522381245204365871L;
	private Long id;
	private String title;
	private String gitBranch;
	private Long userId;
	private Long projectId;
	private Integer flag;
	private Integer status;
	private Date gmtCreate;
	private Date gmtModified;
	
	private Date startDate;
	private Date endDate;
	
    public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	/**
    * 执行日志
    */
	private String log;

	/**
	* 是否分页
	*/
	private Boolean paging = true;

	/**
	* 排序类型
	*/
	private String orderByType = ApplyQuery.OrderByTypeEnum.ID_DESC.getOrderByType();


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

	public Boolean getPaging() {
		return paging;
	}

	public void setPaging(Boolean paging) {
		if (null != paging && paging.booleanValue() == true) {
			this.paging = true;
			return;
		}
		this.paging = null;
	}

	public String getOrderByType() {
		return orderByType;
	}

	public void setOrderByType(ApplyQuery.OrderByTypeEnum enumer) {
		this.orderByType = enumer.getOrderByType();
	}

	public enum OrderByTypeEnum {
		ID_DESC("id desc"),
		GMT_MODIFIED_DESC("gmt_modified desc");

		OrderByTypeEnum(String orderByType) {
			this.orderByType = orderByType;
		}

		private final String orderByType;

		public String getOrderByType() {
			return orderByType;
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
