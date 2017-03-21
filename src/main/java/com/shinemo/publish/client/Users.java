
package com.shinemo.publish.client;

import java.util.Date;

import com.shinemo.publish.common.BaseDO;
public class Users extends BaseDO {
	private static final long serialVersionUID = -318201398572271904L;
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
	* 0普通，1管理员，2 超级管理员
	*/
	private Integer type;
	/**
	* 是否禁用此用户
	*/
	private Integer flag;
	private Date gmtModified;
	private Date gmtCreate;


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
	
}
