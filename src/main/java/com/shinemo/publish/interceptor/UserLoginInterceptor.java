package com.shinemo.publish.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.shinemo.power.client.anno.IgnoreLogin;
import com.shinemo.power.client.common.UserContext;
import com.shinemo.power.client.common.UserContextHolder;
import com.shinemo.power.client.user.domain.User;
import com.shinemo.power.client.util.GsonUtil;
import com.shinemo.power.client.util.RedirectLoginHelper;
import com.shinemo.power.session.UserLoginHelper;
import com.shinemo.power.session.util.SessionKeyUtil;
import com.shinemo.power.session.util.WebUtil;
import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.constants.ProjectConstants;
import com.shinemo.publish.service.UsersService;
import com.shinemo.publish.utils.MailUtil;
import com.shinemo.publish.utils.ServerConfig;
import com.shinemo.publish.utils.UserUtil;

public class UserLoginInterceptor extends HandlerInterceptorAdapter {

	@Resource
	private UsersService usersService;

	// private UserModuleService myaceUserModuleService;
	//
	// private String moduleName;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		boolean hanlderMethodBoolean = handler instanceof HandlerMethod;
		if (!hanlderMethodBoolean) { // 静态资源映射
			return true;
		}

		final Method method = ((HandlerMethod) handler).getMethod();
		final IgnoreLogin ignoreLogin = method.getAnnotation(IgnoreLogin.class);
		if (ignoreLogin != null) { // 不需要登陆
			return true;
		} else { // 需要登陆
			boolean login = UserLoginHelper.isLogin(request);
			if (!login) { // 未登录
				// 需要重定向到登陆页面
				RedirectLoginHelper.sendRedirect(request, response);
				return false;
			} else { // 登陆

				UserContext userContext = new UserContext(
						UserLoginHelper.getUserId(request),
						UserLoginHelper.getNick(request),
						UserLoginHelper.getUserType(request),
						GsonUtil.fromGson2Obj(UserLoginHelper.getAttr(request,
								SessionKeyUtil.ATTRIBUTE_USER_BASE_INFO),
								User.class),
						UserLoginHelper.getUserPhone(request));
				UserContextHolder.put(userContext);
				long userId = userContext.getUserId();
				UsersQuery query = new UsersQuery();
				query.setSsoUserId(userId);
				Result<Users> ret = usersService.get(query);
				if (ret.isSuccess()) {
					Users users = ret.getValue();
					if (users != null) {
						UserUtil.put(request, users);
					} else {// 第一次登录写入数据
					// UserModuleQuery userModuleQuery = new UserModuleQuery();
					// userModuleQuery.setUserId(UserContextHolder.getUserId());
					// userModuleQuery.setModuleName(moduleName);
					// userModuleQuery.setStatus(UserModuleEnum.NORMAL.getId());
					// com.shinemo.power.client.common.Result<UserModule>
					// userModuleResult =
					// getUserModuleServiceClient().get(userModuleQuery);
					//
					// //用户已经授权,则插入db中
					// if(userModuleResult.isSuccess()&&userModuleResult.getValue()!=null){
						users = new Users();
						users.setSsoUserId(userId);
						users.setType(ProjectConstants.USER_TYPE_USER);
						users.setMail(userContext.getUserInfo().getEmail());
						users.setMobile(userContext.getUserInfo().getPhone());
						users.setFlag(ProjectConstants.USER_FLAG_DISABLE);
						users.setName(userContext.getUserName());
						usersService.add(users);
						//新用户发邮件给luohj
						List<String> tos = new ArrayList<String>();
						tos.add("luohj@shinemo.com");
						MailUtil.sendMail("发布系统新用户","新用户"+userContext.getUserName(), "新用户审核:"+userContext.getUserName(), tos);
						UserUtil.put(request, users);
						// }
					}
					String username = java.net.URLEncoder.encode(
							userContext.getUserName(), "utf-8");
					// 写cookie
					WebUtil.addCookie(request, response, "userinfo", 
							String.valueOf(users.getType()) + ":"
									+ username, ServerConfig
									.get("cookie.domain", "jituancaiyun.com"),"/", -1, false);
					

					String cookieValue = WebUtil.findCookieValue(request,
							"_scn");
					if (null != cookieValue) {
						WebUtil.addCookie(request, response, "_sid",
								cookieValue, ServerConfig
								.get("cookie.domain", "jituancaiyun.com"),"/", 1 * 60 * 60,false);
					}

					if (ProjectConstants.USER_FLAG_ENABLE != users.getFlag()) {
						// 用户状态不正常
						((HttpServletResponse) response)
								.sendRedirect("/publish/invalid.htm");
					}
				}
				return true;
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		UserContextHolder.clearContext();

	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	// public String getModuleName() {
	// return moduleName;
	// }
	//
	// public void setModuleName(String moduleName) {
	// this.moduleName = moduleName;
	// }
	//
	// public UserModuleService getUserModuleServiceClient() {
	// if(myaceUserModuleService == null){
	// myaceUserModuleService =
	// SpringContextHolder.getBean("myaceUserModuleService",
	// UserModuleService.class);
	// }
	// return myaceUserModuleService;
	// }

}
