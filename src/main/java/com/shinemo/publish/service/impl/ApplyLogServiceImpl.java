package com.shinemo.publish.service.impl;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.shinemo.publish.client.ApplyLog;
import com.shinemo.publish.client.ApplyLogQuery;
import com.shinemo.publish.common.CommonService;
import com.shinemo.publish.common.Mapper;
import com.shinemo.publish.mapper.ApplyLogMapper;
import com.shinemo.publish.service.ApplyLogService;
import com.shinemo.publish.utils.Requests;
import com.shinemo.publish.utils.UserUtil;

@Service("applyLogService")
public class ApplyLogServiceImpl extends CommonService<ApplyLogQuery, ApplyLog> implements ApplyLogService{

	@Resource
	private ApplyLogMapper applyLogMapper;
	
	@Override
	public Mapper<ApplyLogQuery, ApplyLog> getMapper() {
		return applyLogMapper;
	}

	@Override
	public Long getId(ApplyLog temp) {
		return temp.getId();
	}

	@Override
	public void addLog(long applyId, int step, String stepName) {
		ApplyLog applyLog = new ApplyLog();
    	applyLog.setStep(step);
    	applyLog.setApplyId(applyId);
    	applyLog.setStepName(stepName);
    	HttpServletRequest request = Requests.getRequest();
    	applyLog.setUserId(UserUtil.getUid(request));
    	applyLog.setUsername(UserUtil.getUserName(request));
		add(applyLog);
		
	}




}
