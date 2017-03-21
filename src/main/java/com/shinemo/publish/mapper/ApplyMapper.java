package com.shinemo.publish.mapper;

import java.util.List;

import com.shinemo.publish.client.Apply;
import com.shinemo.publish.client.ApplyQuery;
import com.shinemo.publish.common.Mapper;


public interface ApplyMapper extends Mapper<ApplyQuery, Apply>{
	
	
	/**
	 * 获取需要审核的列表
	 * @param query
	 * @return
	 */
	public List<Apply> getNewApplys(ApplyQuery query);

	/**
	 * 获取需要审核的记录数
	 * @param query
	 * @return
	 */
	public Long getNewApplysCount(ApplyQuery query);
	
	
	/**
	 * 某个Project下 git一样的 正在处理的发布单，
	 * @param query
	 * @return
	 */
	public List<Apply> getProcessingApply(ApplyQuery query);
}


