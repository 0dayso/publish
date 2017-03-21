package com.shinemo.publish.client;

import java.util.Date;

import com.shinemo.publish.common.QueryBase;

public class ProjectQuery extends QueryBase {
	private static final long serialVersionUID = 3377478369709596345L;
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
	
	private Integer env;
	
	private Integer stat;
	
	
	public Integer getStat() {
		return stat;
	}

	public void setStat(Integer stat) {
		this.stat = stat;
	}

	public Integer getEnv() {
		return env;
	}

	public void setEnv(Integer env) {
		this.env = env;
	}

	/**
	* 是否分页
	*/
	private Boolean paging = true;

	/**
	* 排序类型
	*/
	private String orderByType = ProjectQuery.OrderByTypeEnum.ID_DESC.getOrderByType();


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

	public void setOrderByType(ProjectQuery.OrderByTypeEnum enumer) {
		this.orderByType = enumer.getOrderByType();
	}

	public enum OrderByTypeEnum {
		ID_DESC("id desc"),
		ENV_ASC("env asc");

		OrderByTypeEnum(String orderByType) {
			this.orderByType = orderByType;
		}

		private final String orderByType;

		public String getOrderByType() {
			return orderByType;
		}
	}
}
