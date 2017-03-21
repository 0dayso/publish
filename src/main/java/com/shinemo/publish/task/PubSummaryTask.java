package com.shinemo.publish.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shinemo.power.client.util.DateUtil;
import com.shinemo.publish.client.Apply;
import com.shinemo.publish.client.ApplyQuery;
import com.shinemo.publish.client.ApplyQuery.OrderByTypeEnum;
import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.constants.ProjectConstants;
import com.shinemo.publish.debug.vm.VMAttr;
import com.shinemo.publish.debug.vm.VMQueue;
import com.shinemo.publish.debug.websocket.DebugSocketEcho;
import com.shinemo.publish.service.ApplyService;
import com.shinemo.publish.service.ProjectService;
import com.shinemo.publish.service.UsersService;
import com.shinemo.publish.utils.DateUtils;
import com.shinemo.publish.utils.LockUtils;
import com.shinemo.publish.utils.MailServiceClient;
import com.shinemo.publish.utils.MailUtil;
import com.shinemo.publish.utils.ServerConfig;

/**
 * 
 * @author figo 2016年6月2日
 */
@Component
public class PubSummaryTask {
	// "0 0 12 * * ?" 每天中午12点触发
	// "0 15 10 ? * *" 每天上午10:15触发
	// "0 15 10 * * ?" 每天上午10:15触发
	// "0 15 10 * * ? *" 每天上午10:15触发
	// "0 15 10 * * ? 2005" 2005年的每天上午10:15触发
	// "0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
	// "0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发
	// "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
	// "0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
	// "0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发
	// "0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发
	// "0 15 10 15 * ?" 每月15日上午10:15触发
	// "0 15 10 L * ?" 每月最后一日的上午10:15触发
	// "0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
	// "0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发
	// "0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发

	@Resource
	private ApplyService applyService;
	@Resource
	private ProjectService projectService;
	@Resource
	private UsersService usersService;

	private static final Logger logger = LoggerFactory
			.getLogger(PubSummaryTask.class);

	/**
	 * 定时计算。每天凌晨 01:00 执行一次
	 */
	@Scheduled(cron = "0 */3 * * * *")
	public void checkVm() {
		if (LockUtils.get()){
			logger.info("check debug task!");
			//检查过期的vm
			Map<String,VMAttr> map = VMQueue.VMMAP;
			Set<Map.Entry<String,VMAttr>> set  = map.entrySet();
			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
				Map.Entry<String, VMAttr> entry = (Map.Entry<String, VMAttr>) iterator
						.next();
				VMAttr vm = entry.getValue();
				if(DateUtils.isExpired(vm.getLastTime(), 5)){
					String key = vm.getSid();
					ResultVO result = ResultVO.success("connect timeout , release a vm", DebugSocketEcho.RELEASE_CODE);
					DebugSocketEcho.processText("connect timeout , release a vm", vm.getUid());
					DebugSocketEcho.processText("debug vm has release", vm.getUid());
					DebugSocketEcho.processResult(result, vm.getSid());
					VMQueue.release(key, vm.getUid());
					logger.info("release a vm:"+vm.toString());
				}else{
					logger.info("this vm is debuging... :"+vm.toString());
				}
			}
		}
	}

	// 发布记录邮件
	/**
	 * 定时计算。每周一 8：8：8 执行一次
	 */
	@Scheduled(cron = "8 8 8 * * 1")
	public void sendMail() {
		if (LockUtils.get()) {
			Date before7day = DateUtils.getDayPlus(-7);
			Date now = new Date();
			ApplyQuery query = new ApplyQuery();
			query.setStartDate(before7day);
			query.setEndDate(now);
			query.setFlag(ProjectConstants.APPLY_FLAG_NORMAL);
			query.setPageSize(40);
			ApplyQuery.OrderByTypeEnum enumer = OrderByTypeEnum.GMT_MODIFIED_DESC	;//new ApplyQuery.OrderByTypeEnum("gmt_modified desc ");
			query.setOrderByType(enumer);
			Result<List<Apply>> r = applyService.find(query);

			String title = ServerConfig.get("mail.from", "")+"一周发布汇总";
			StringBuffer content = new StringBuffer();
			content.append("\n<b>上周发布总数：</b><font color=red>").append(r.getRemark())
			.append("</font>\n");
			if (r.isSuccess() && r.getValue() != null ) {
				if(r.getValue().size()==0){
					return;
				}
				//title
				content.append("<table bordercolor=\"#008888\" style=\"BORDER-COLLAPSE: collapse\" border=1>");
				content.append("<tr>").append("<td>");
				content.append("seq").append("</td><td>").append("User").append("</td><td>")
						.append("Time").append("</td><td>")
						.append("Status")
						.append("</td><td>").append("Title")
						.append("</td>");
				content.append("</tr>");
				
				int index = 1;
				for (Apply apply : r.getValue()) {
					ProjectQuery pq = new ProjectQuery();
					pq.setId(apply.getProjectId());
					Project p = projectService.get(pq).getValue();

					UsersQuery uq = new UsersQuery();
					uq.setId(apply.getUserId());
					Users u = usersService.get(uq).getValue();

					content.append("<tr>").append("<td>");
					content.append(index++).append("</td><td>").append(u.getName()).append("</td><td>")
							.append(DateUtil.format(apply.getGmtModified())).append("</td><td>")
							.append(ProjectConstants.parseStatus(apply
									.getStatus()))
							.append("</td><td>").append(p.getTitle()+"-"+apply.getTitle())
							.append("</td>");
					content.append("</tr>");
					if (index > 40) {
						break;
					}
				}
				content.append("</table>");
				content.append("\n\n 更多:\t")
				.append("https://publish.tools.jituancaiyun.com/publish/index.html\n<br>");
			}
			List<String> tos = new ArrayList<String>();
			tos.add(ServerConfig.get("mail.to","luohj@shinemo.com"));
			try {
				MailUtil.sendTencentMail(title, content.toString(), tos);
			} catch (Exception e) {
				try {
					MailServiceClient.send(ServerConfig.get("mail.to","luohj@shinemo.com"), "internal_notice@shinemo.com", "发布通知", title, content.toString());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				e.printStackTrace();
			}
			
			
		}
	}
	
	
	public static void main(String[] args) {

		String title = "一周发布汇总";
		StringBuffer content = new StringBuffer();
		content.append("\n<b>上周发布总数：</b><font color=red>").append(5)
				.append("</font>\n<br>");
		
		content.append("<table bordercolor=\"#008888\" style=\"BORDER-COLLAPSE: collapse\" border=1>");
		content.append("<tr>").append("<td>");
		content.append("seq").append("</td><td>").append("User").append("</td><td>")
				.append("Time").append("</td><td>")
				.append("Status")
				.append("</td><td>").append("Title")
				.append("</td>");
		content.append("</tr>");
		int index = 1;
		for (long i = 0; i < 5; i++) {
			ProjectQuery pq = new ProjectQuery();
			pq.setId(i);
			content.append("<tr>").append("<td>");
			content.append(index++).append("</td><td>").append("测试" + i).append("</td><td>")
					.append(DateUtil.format(new Date())).append("</td><td>")
					.append(ProjectConstants.parseStatus((int) (i + 5)))
					.append("</td><td>").append("dd等待").append("-").append(i%2==0?"标题":"标题很长很长很长很长很长很长" + 1)
					.append("</td>");
			content.append("</tr>");
		}
		content.append("</table>");
		content.append("\n\n更多:\t\r")
				.append("https://publish.tools.jituancaiyun.com/publish/index.html\n<br></body>");
		List<String> tos = new ArrayList<String>();
		tos.add("luohj@shinemo.com");
		MailServiceClient.send("luohj@shinemo.com", "internal_notice@shinemo.com", "发布通知", title, content.toString());
		//MailUtil.sendTencentMail(title, content.toString(), tos);
	}

	// 每周发布报表

}
