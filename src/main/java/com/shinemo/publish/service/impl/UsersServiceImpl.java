package com.shinemo.publish.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.CommonService;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Mapper;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultBuilder;
import com.shinemo.publish.mapper.UsersMapper;
import com.shinemo.publish.service.UsersService;

@Service("usersService")
public class UsersServiceImpl extends CommonService<UsersQuery, Users> implements UsersService{

	@Resource
	private UsersMapper usersMapper;
	
	@Override
	public Mapper<UsersQuery, Users> getMapper() {
		return usersMapper;
	}

	@Override
	public Long getId(Users temp) {
		return temp.getId();
	}

	@Override
	public Result<List<Users>> listByName(String keyword, int limit) {
		ResultBuilder<List<Users>> builder = new ResultBuilder<List<Users>>();
		try {
			Map map = new HashMap();
			if(keyword!=null && !"".equals(keyword)){
				map.put("keyword", keyword);
			}
			map.put("limit", limit);
			List<Users> ret = usersMapper.listByName(map);
			builder.setValue(ret);
			builder.setSuccess(true);
		} catch (Exception e) {
			builder.setSuccess(false).setError(Errors.ERROR_500)
					.setThrowable(e);
		}
		return builder.build();
	}

}
