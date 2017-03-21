package com.shinemo.publish.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.shinemo.publish.client.Apply;
import com.shinemo.publish.client.ApplyQuery;
import com.shinemo.publish.common.CommonService;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Mapper;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultBuilder;
import com.shinemo.publish.mapper.ApplyMapper;
import com.shinemo.publish.service.ApplyService;

@Service("applyService")
public class ApplyServiceImpl extends CommonService<ApplyQuery, Apply>
		implements ApplyService {

	@Resource
	private ApplyMapper applyMapper;

	@Override
	public Mapper<ApplyQuery, Apply> getMapper() {
		return applyMapper;
	}

	@Override
	public Long getId(Apply temp) {
		return temp.getId();
	}

	@Override
	public Result<List<Apply>> getNewApplys(Long userId,int cPage ,int pSize) {
		ApplyQuery query = new ApplyQuery();
		query.setUserId(userId);
		query.setCurrentPage(cPage);
		query.setPageSize(pSize);
		ResultBuilder<List<Apply>> builder = new ResultBuilder<List<Apply>>();
		try {
			List<Apply> ret = applyMapper.getNewApplys(query);
			builder.setValue(ret);
			builder.setSuccess(true);
		} catch (Exception e) {
			builder.setSuccess(false).setError(Errors.ERROR_500)
					.setThrowable(e);
		}
		return builder.build();
	}
	
	
	@Override
	public Result<Long> getNewApplysCount(Long userId) {
		ApplyQuery query = new ApplyQuery();
		query.setUserId(userId);

		ResultBuilder<Long> builder = new ResultBuilder<Long>();
		try {
			Long ret = applyMapper.getNewApplysCount(query);
			builder.setValue(ret);
			builder.setSuccess(true);
		} catch (Exception e) {
			builder.setSuccess(false).setError(Errors.ERROR_500)
					.setThrowable(e);
		}
		return builder.build();
	}

	@Override
	public Result<List<Apply>> getProcessingApply(Long projectId,Date gmtModified) {
		ApplyQuery query = new ApplyQuery();
		query.setProjectId(projectId);
		//query.setStartDate(gmtModified);
		query.setGmtModified(gmtModified);

		ResultBuilder<List<Apply>> builder = new ResultBuilder<List<Apply>>();
		try {
			List<Apply> ret = applyMapper.getProcessingApply(query);
			builder.setValue(ret);
			builder.setSuccess(true);
		} catch (Exception e) {
			builder.setSuccess(false).setError(Errors.ERROR_500)
					.setThrowable(e);
		}
		return builder.build();
	}

}
