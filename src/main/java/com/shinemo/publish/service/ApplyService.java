package com.shinemo.publish.service;

import java.util.Date;
import java.util.List;

import com.shinemo.publish.client.Apply;
import com.shinemo.publish.client.ApplyQuery;
import com.shinemo.publish.common.BaseService;
import com.shinemo.publish.common.Result;

public interface ApplyService extends BaseService<ApplyQuery, Apply>{
	
	/**
	 * 获取这个用户需要审核的发布单
	 * @param userId
	 * @return
	 */
	Result<List<Apply>> getNewApplys(Long userId,int cPage ,int pSize);
	
	 
	/**
	 * 获取这个用户需要审核的发布单
	 * @param userId
	 * @return
	 */
	Result<Long> getNewApplysCount(Long userId);
	
	
	/**
	 * 正在处理中的发布单
	 * @param projectId
	 * @return
	 */
	Result<List<Apply>> getProcessingApply(Long projectId,Date gmtModified);
	
	

}
