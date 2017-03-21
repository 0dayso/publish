package com.shinemo.publish.service.impl;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.shinemo.publish.client.ProjectUser;
import com.shinemo.publish.client.ProjectUserQuery;
import com.shinemo.publish.common.CommonService;
import com.shinemo.publish.common.Mapper;
import com.shinemo.publish.mapper.ProjectUserMapper;
import com.shinemo.publish.service.ProjectUserService;

@Service("projectUserService")
public class ProjectUserServiceImpl extends CommonService<ProjectUserQuery, ProjectUser> implements ProjectUserService{

	@Resource
	private ProjectUserMapper projectUserMapper;
	
	@Override
	public Mapper<ProjectUserQuery, ProjectUser> getMapper() {
		return projectUserMapper;
	}

	@Override
	public Long getId(ProjectUser temp) {
		return temp.getId();
	}



}
