package com.shinemo.publish.mapper;

import java.util.List;
import java.util.Map;

import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.common.Mapper;


public interface ProjectMapper extends Mapper<ProjectQuery, Project>{
	
	List<Project> listByUid(Map map);
}


