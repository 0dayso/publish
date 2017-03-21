package com.shinemo.publish.client;

import java.util.Date;

import com.shinemo.Aace.msgcenter.client.PushMsgClient;
import com.shinemo.publish.common.QueryBase;
public class ApplyLogQuery extends QueryBase {
	private static final long serialVersionUID = 4259353907664664935L;
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
    /**
    * 具体步骤，定义在java
    */
	private Long applyId;
	private Integer step;
	private String stepName;
	private String log;
	private Long userId;
	private String username;

	/**
	* 是否分页
	*/
	private Boolean paging = true;

	/**
	* 排序类型
	*/
	private String orderByType = ApplyLogQuery.OrderByTypeEnum.ID_DESC.getOrderByType();


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getApplyId() {
		return applyId;
	}

	public void setApplyId(Long applyId) {
		this.applyId = applyId;
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

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public void setOrderByType(ApplyLogQuery.OrderByTypeEnum enumer) {
		this.orderByType = enumer.getOrderByType();
	}

	public enum OrderByTypeEnum {
		ID_DESC("id desc");

		OrderByTypeEnum(String orderByType) {
			this.orderByType = orderByType;
		}
		
		private final String orderByType;

		public String getOrderByType() {
			PushMsgClient p = new PushMsgClient();
			return orderByType;
		}
	}
}
