package com.shinemo.publish.service;

import com.shinemo.publish.client.ApplyLog;
import com.shinemo.publish.client.ApplyLogQuery;
import com.shinemo.publish.common.BaseService;

public interface ApplyLogService extends BaseService<ApplyLogQuery, ApplyLog>{
	
	void addLog(long applyId, int step,String stepName);

}
