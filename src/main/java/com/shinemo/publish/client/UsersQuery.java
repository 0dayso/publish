package com.shinemo.publish.client;

import java.util.Date;

import com.shinemo.publish.common.QueryBase;
public class UsersQuery extends QueryBase {
	private static final long serialVersionUID = -1670344449598942133L;
	private Long id;
    /**
    * 来自acl系统的用户ID
    */
	private Long ssoUserId;
	private String mobile;
	private String mail;
	private String name;
	private String role;
    /**
    * 0，1，2
    */
	private Integer type;
    /**
    * 是否禁用此用户
    */
	private Integer flag;
	private Date gmtModified;
	private Date gmtCreate;

	/**
	* 是否分页
	*/
	private Boolean paging = true;

	/**
	* 排序类型
	*/
	private String orderByType = UsersQuery.OrderByTypeEnum.ID_DESC.getOrderByType();


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSsoUserId() {
		return ssoUserId;
	}

	public void setSsoUserId(Long ssoUserId) {
		this.ssoUserId = ssoUserId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
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

	public void setOrderByType(UsersQuery.OrderByTypeEnum enumer) {
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
