package com.shinemo.publish.mapper;

import java.util.List;
import java.util.Map;

import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.Mapper;


public interface UsersMapper extends Mapper<UsersQuery, Users>{
	
	List<Users> listByName(Map map);
}


