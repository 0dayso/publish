package com.shinemo.publish.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.client.Users;
import com.shinemo.publish.common.CommonService;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Mapper;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultBuilder;
import com.shinemo.publish.mapper.ProjectMapper;
import com.shinemo.publish.service.ProjectService;

@Service("projectService")
public class ProjectServiceImpl extends CommonService<ProjectQuery, Project> implements ProjectService{

	@Resource
	private ProjectMapper projectMapper;
	
	@Override
	public Mapper<ProjectQuery, Project> getMapper() {
		return projectMapper;
	}

	@Override
	public Long getId(Project temp) {
		return temp.getId();
	}

	@Override
	public Result<List<Project>> listByUid(Long userId, int limit) {
		ResultBuilder<List<Project>> builder = new ResultBuilder<List<Project>>();
		try {
			Map map = new HashMap();
			map.put("userId", userId);
			map.put("limit", limit);
			List<Project> ret = projectMapper.listByUid(map);
			builder.setValue(ret);
			builder.setSuccess(true);
		} catch (Exception e) {
			builder.setSuccess(false).setError(Errors.ERROR_500)
					.setThrowable(e);
		}
		return builder.build();
	}



}
