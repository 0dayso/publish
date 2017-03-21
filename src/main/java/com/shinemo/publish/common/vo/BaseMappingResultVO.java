package com.shinemo.publish.common.vo;

import java.util.List;

public class BaseMappingResultVO {
	private List<BaseLastMappingVO> voList;
	private Long lastModified;
	private Integer isLast;
	
	public List<BaseLastMappingVO> getVoList() {
		return voList;
	}
	public void setVoList(List<BaseLastMappingVO> voList) {
		this.voList = voList;
	}
	public Long getLastModified() {
		return lastModified;
	}
	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}
	public Integer getIsLast() {
		return isLast;
	}
	public void setIsLast(Integer isLast) {
		this.isLast = isLast;
	}
	
	

}
