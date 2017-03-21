package com.shinemo.publish.client;

import java.util.Date;

import com.shinemo.publish.common.QueryBase;

public class ProjectUserQuery extends QueryBase {
	private static final long serialVersionUID = 4843751405359883649L;
	private Long id;
	private Long projectId;
	private Long userId;
    /**
    * 0：普通  1：管理员
    */
	private Integer type;
	private Date gmtCreate;
	private Date gmtModified;

	/**
	* 是否分页
	*/
	private Boolean paging = true;

	/**
	* 排序类型
	*/
	private String orderByType = ProjectUserQuery.OrderByTypeEnum.ID_DESC.getOrderByType();


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public void setOrderByType(ProjectUserQuery.OrderByTypeEnum enumer) {
		this.orderByType = enumer.getOrderByType();
	}

	public enum OrderByTypeEnum {
		ID_DESC("id desc");

		OrderByTypeEnum(String orderByType) {
			this.orderByType = orderByType;
		}

		private final String orderByType;

		public String getOrderByType() {
			return orderByType;
		}
	}
}
