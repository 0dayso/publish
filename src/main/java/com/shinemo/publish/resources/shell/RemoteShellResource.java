package com.shinemo.publish.resources.shell;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shinemo.power.session.util.WebUtil;
import com.shinemo.publish.common.BaseResource;
import com.shinemo.publish.common.Errors;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.constants.ServerConstants;
import com.shinemo.publish.redis.service.RedisService;
import com.shinemo.publish.service.ApplyLogService;
import com.shinemo.publish.service.ApplyService;
import com.shinemo.publish.service.ProjectService;
import com.shinemo.publish.service.ProjectUserService;
import com.shinemo.publish.service.ShellService;
import com.shinemo.publish.utils.CMDUtils;
import com.shinemo.publish.utils.ServerConfig;
import com.shinemo.publish.utils.UserUtil;

@RequestMapping("/remoteshell")
@Controller
public class RemoteShellResource extends BaseResource {

	private static final Logger LOG = LoggerFactory
			.getLogger(RemoteShellResource.class);

	@Resource
	private ProjectService projectService;

	@Resource
	private ApplyService applyService;

	@Resource
	private ApplyLogService applyLogService;

	@Resource
	private ShellService shellService;
	
	@Resource
	private ProjectUserService projectUserService;
	
	
	@Resource
    private RedisService redisService;

	/**
	 * for test
	 * 
	 * @param request
	 * @param cmd
	 * @return
	 */
//	@RequestMapping(value = "/checkShell", method = RequestMethod.GET)
//	@ResponseBody
//	public ResultVO<String> checkShell(@Context HttpServletRequest request,
//			@QueryParam("cmd") String cmd) {
//		//String[] cmds = new String[]{"sh","-c",cmd};
//		Result<String> ret = shellService.execRemoteShell(cmd, "shinemo", "", "10.161.208.135", 9092);
//		return buildResultVO(ret);
//	}
	
	
	/**
	 * 获取 日志并展示
	 * 
	 * @param logpath
	 *            日志绝对路径
	 * @param lines
	 *            取日志行数，default 1000 MAX 5000
	 * @return
	 */
	@RequestMapping(value = "/getlog")
	@ResponseBody
	public ResultVO<String> getlog(@Context HttpServletRequest request,
			@QueryParam("logpath") String logpath,
			@QueryParam("lines") int lines) {
		if (lines == 0) {
			lines = 1000;
		}
		if (lines > 5000) {
			lines = 5000;
		}

		Result<String> ret = shellService.execLocalShell("tail -n " + lines
				+ " " + logpath);
		return buildResultVO(ret);
	}
	
	
	
	@RequestMapping(value = "/getRemotelog")
	@ResponseBody
	public ResultVO<String> getRemotelog(@Context HttpServletRequest request,
			@QueryParam("logpath") String logpath,
			@QueryParam("lines") int lines, @QueryParam("host") String host,
			@QueryParam("port") int port) {
		if (lines == 0) {
			lines = 1000;
		}
		if (lines > 5000) {
			lines = 5000;
		}
		String command = "tail -n " + lines + " " + logpath ;
		Result<String> ret = shellService.execRemoteShell(command, "shinemo-safe", "", host, port);
//		Result<String> ret = shellService.execLocalShell("ssh " + host + " -p "
//				+ port + " \"tail -n " + lines + " " + logpath + "\"");
		return buildResultVO(ret);
	}

	
	

	
	/***   管理员才能执行    ***/
	
	@RequestMapping(value = "/allHost")
	@ResponseBody
	public ResultVO allHost(@Context HttpServletRequest request,@Context HttpServletResponse response) {
		
			return ResultVO.success(ServerConstants.sortMapByValue());
	}
	
	
	@RequestMapping(value = "/selectHost")
	@ResponseBody
	public ResultVO selectHost(@Context HttpServletRequest request,@Context HttpServletResponse response,
			@QueryParam("hostinfo") String hostinfo) {
		
		if(UserUtil.isAdmin(request) && hostinfo!=null){
			//redisService.set("pub-host:"+UserContextHolder.get().getUserId(), hostinfo);
			WebUtil.addCookie(request, response, "hostinfo", hostinfo, ServerConfig.get("cookie.domain","jituancaiyun.com"),-1,false);
		}else{
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		
		return ResultVO.success("ok");
	}
	
	
	@RequestMapping(value = "/cmd")
	@ResponseBody
	public ResultVO<String> cmd(@Context HttpServletRequest request,
			@QueryParam("cmd") String cmd) {
		if(!UserUtil.isAdmin(request)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		if(!CMDUtils.isAllow(cmd)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		String hostinfo = WebUtil.findCookieValue(request, "hostinfo");
		if(hostinfo!=null){
			String[] hosts = hostinfo.split(":");
			Result<String> ret = shellService.execRemoteShell(cmd, hosts[0], "", hosts[1], Integer.parseInt(hosts[2]));
			return buildResultVO(ret);
		}
		return ResultVO.fall(Errors.E_UN_KNOWN);
	}
	
	
	@RequestMapping(value = "/cmdList")
	@ResponseBody
	public ResultVO<String> cmdList(@Context HttpServletRequest request) {
		if(!UserUtil.isAdmin(request)){
			return ResultVO.fall(Errors.E_NO_AUTH);
		}
		String hostinfo = WebUtil.findCookieValue(request, "hostinfo");
		if(hostinfo!=null){
			String[] hosts = hostinfo.split(":");
			List<String> ret = CMDUtils.getCmds();
			return buildResultVO(new Result().setSuccess(true).setValue(ret));
		}
		return ResultVO.fall(Errors.E_UN_KNOWN);
	}
	
}
