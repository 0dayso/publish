package com.shinemo.publish.service;

import java.util.List;

import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.BaseService;
import com.shinemo.publish.common.Result;

public interface UsersService extends BaseService<UsersQuery,Users>{
	
	/**
	 * 获取这个用户需要审核的发布单
	 * @param userId
	 * @return
	 */
	Result<List<Users>> listByName(String keyword,int limit);

}
