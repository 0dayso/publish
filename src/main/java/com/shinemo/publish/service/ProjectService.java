package com.shinemo.publish.service;

import java.util.List;

import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.client.Users;
import com.shinemo.publish.common.BaseService;
import com.shinemo.publish.common.Result;

public interface ProjectService extends BaseService<ProjectQuery,Project>{

	/**
	 * 获取这个用户的可见发布单
	 * @return
	 */
	Result<List<Project>> listByUid(Long userId,int limit);
}
