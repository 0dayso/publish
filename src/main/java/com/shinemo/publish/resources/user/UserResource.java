package com.shinemo.publish.resources.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.power.client.common.UserContext;
import com.shinemo.power.client.common.UserContextHolder;
import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.BaseResource;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.constants.ProjectConstants;
import com.shinemo.publish.service.UsersService;
import com.shinemo.publish.utils.Requests;
import com.shinemo.publish.utils.UserUtil;

@RequestMapping("/user")
@Controller
public class UserResource extends BaseResource {

	private String ID = "id";
	private String ROLE = "role";
	private String TYPE = "type";
	private String FLAG = "flag";

	@Resource
	private UsersService usersService;

	@RequestMapping(value = "/me", method = RequestMethod.GET)
	@ResponseBody
	public ResultVO<Users> me() {
		UserContext user = UserContextHolder.get();
		UsersQuery query = new UsersQuery();
		query.setSsoUserId(user.getUserId());
		Result<Users> ret = usersService.get(query);
		return buildResultVO(ret);
	}

	// 获取用户信息
	@RequestMapping(value = "/get")
	@ResponseBody
	public ResultVO<Users> get(@Context HttpServletRequest request,
			@QueryParam("id") long id) {
		if (!UserUtil.isSuperAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		UserContext user = UserContextHolder.get();
		UsersQuery query = new UsersQuery();
		query.setId(id);
		Result<Users> ret = usersService.get(query);
		return buildResultVO(ret);
	}

	@RequestMapping(value = "/listByName", method = RequestMethod.GET)
	@ResponseBody
	public ResultVO listByName(@Context HttpServletRequest request,
			@RequestParam(value = "limit", defaultValue = "100") int limit,
			@RequestParam(value = "keyword") String keyword) {
		if (!UserUtil.isSuperAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		Result<List<Users>> ret = usersService.listByName(keyword, limit);
		UsersQuery queryc = new UsersQuery();
		if (ret.isSuccess()) {
			Map map = new HashMap();
			map.put("list", ret.getValue());
			Result<Map> result = new Result<Map>(true, map, 200, null);
			return buildResultVO(result);
		}

		return ResultVO.fall(200, Errors.ERROR_500.getMsg());
	}

	@RequestMapping(value = "/listall", method = RequestMethod.GET)
	@ResponseBody
	public ResultVO listall(@Context HttpServletRequest request) {
		if (!UserUtil.isAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		UsersQuery query = new UsersQuery();
		query.setFlag(ProjectConstants.USER_FLAG_ENABLE);
		query.setPageSize(100);
		Result<List<Users>> ret = usersService.find(query);
		if (ret.isSuccess()) {
			List resultList = new ArrayList();
			for (Users users : ret.getValue()) {
				Map map = new HashMap();
				map.put("key", users.getName());
				map.put("value", users.getId());
				resultList.add(map);
			}

			return buildResultVO((new Result()).setSuccess(true).setValue(
					resultList));
		}
		return ResultVO.fall(200, Errors.ERROR_500.getMsg());
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public ResultVO list(
			@Context HttpServletRequest request,
			@RequestParam(value = "pageCount", defaultValue = "1") int pageCount,
			@RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
		if (!UserUtil.isSuperAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		UsersQuery query = new UsersQuery();
		query.setCurrentPage(pageCount);
		query.setPageSize(pageSize);
		Result<List<Users>> ret = usersService.find(query);
		UsersQuery queryc = new UsersQuery();
		Result<Long> rcount = usersService.count(queryc);
		if (ret.isSuccess() && rcount.isSuccess()) {
			Map map = new HashMap();
			map.put("count", rcount.getValue());
			map.put("list", ret.getValue());
			Result<Map> result = new Result<Map>(true, map, 200, null);
			return buildResultVO(result);
		}

		return ResultVO.fall(200, Errors.ERROR_500.getMsg());
	}

	@RequestMapping(value = "/selectAll", method = RequestMethod.GET)
	@ResponseBody
	public ResultVO selectAll(@Context HttpServletRequest request) {
		if (!UserUtil.isAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		UsersQuery query = new UsersQuery();
		query.setPageSize(100);
		Result<List<Users>> ret = usersService.find(query);
		if (ret.isSuccess()) {
			List rest = new ArrayList();
			List<Users> userList = ret.getValue();
			if (userList != null) {
				for (Users users : userList) {
					Map map = new HashMap();
					map.put("text", users.getName());
					map.put("value", users.getId());
					rest.add(map);
				}
			}

			Result<List> result = new Result<List>(true, rest, 200, null);
			return buildResultVO(result);
		}

		return ResultVO.fall(200, Errors.ERROR_500.getMsg());
	}

	// 编辑用户状态
	@RequestMapping(value = "/edit")
	@ResponseBody
	public ResultVO<Boolean> edit(@Context HttpServletRequest request,
			@QueryParam("id") long id) {
		if (!UserUtil.isSuperAdmin(request)) {
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		UsersQuery query = new UsersQuery();
		query.setId(id);
		Result<Users> result = usersService.get(query);
		Users u = new Users();
		if (result.isSuccess()) {
			u = result.getValue();
		}
		if (u == null) {
			return ResultVO.fall(Errors.E_NOTEXIST);
		}
		u.setFlag(Requests.getInt(FLAG));
		u.setType(Requests.getInt(TYPE));
		u.setRole(Requests.getString(ROLE));
		Result<Boolean> ret = usersService.update(u);
		return buildResultVO(ret);
	}

	/**
	 * 1 用户管理
	 * 2 项目管理
	 * 3 我的发布单
	 * 4 审核发布单
	 * 5 发布记录
	 * 6 webshell
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getMenu")
	@ResponseBody
	public ResultVO<List<Integer>> getMenu(@Context HttpServletRequest request) {
		Users users = UserUtil.get(request);
		List<Integer> menu = new ArrayList<Integer>();
		if(users!=null){
			if(users.getType()>=0){	//普通
				menu.add(3);
				menu.add(4);
				menu.add(6);
			}
			if(users.getType()>=1){	//管理员
				menu.add(2);
				menu.add(5);
			}
			if(users.getType()>=2){	//超级管理员
				menu.add(1);
			}
		}
		
		return ResultVO.success(menu);
	}}
