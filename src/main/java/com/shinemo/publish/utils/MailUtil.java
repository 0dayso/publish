/**
 * 
 */
package com.shinemo.publish.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message.RecipientType;

import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.api.util.SpringUtil;
import com.shinemo.publish.client.Apply;
import com.shinemo.publish.client.ApplyQuery;
import com.shinemo.publish.client.Project;
import com.shinemo.publish.client.ProjectQuery;
import com.shinemo.publish.client.ProjectUser;
import com.shinemo.publish.client.ProjectUserQuery;
import com.shinemo.publish.client.Users;
import com.shinemo.publish.client.UsersQuery;
import com.shinemo.publish.common.Result;
import com.shinemo.publish.constants.ProjectConstants;
import com.shinemo.publish.service.ApplyService;
import com.shinemo.publish.service.ProjectService;
import com.shinemo.publish.service.ProjectUserService;
import com.shinemo.publish.service.UsersService;

/**
 * 腾讯企业邮箱邮件发送服务
 * 
 * @author david
 *
 */
public class MailUtil {
	
	private static final String tencentFrom = "internal_notice@shinemo.com";
	private static final String tencentFromUser = "internal_notice@shinemo.com";
	private static final String tencentFromUserPassward = "ServiceShineMo20132";
	private static final String tencentHost = "smtp.shinemo.com";
	private static final int tencentPort = 465;
	private static ExecutorService mailSendPool = Executors.newFixedThreadPool(10);

	private static final Logger logger = LoggerFactory
			.getLogger(MailUtil.class);
	/**
	 * 
	 * @param title
	 * @param content
	 * @param tos
	 */
	public static void sendTencentMail(final String title, final String content, final List<String> tos){
		mailSendPool.submit(new Runnable(){
			@Override
			public void run() {
				Email email = new Email();
				email.setFromAddress("发布通知", tencentFrom);
				for(String to: tos){
					email.addRecipient(to, to, RecipientType.TO);
				}
				email.setTextHTML(content);
				email.setSubject(title);
				new Mailer(tencentHost, tencentPort, tencentFromUser, tencentFromUserPassward, TransportStrategy.SMTP_SSL).sendMail(email);
			
			}
		});
	}
	
	
	/**
	 * 
	 * @param title
	 * @param content
	 * @param tos
	 */
	public static void sendMail(final String from ,final String title, final String content, final List<String> tos){
		
		mailSendPool.submit(new Runnable(){
			@Override
			public void run() {
				Email email = new Email();
				email.setFromAddress(from, tencentFrom);
				for(String to: tos){
					email.addRecipient(to, to, RecipientType.TO);
				}
				email.setTextHTML(content);
				email.setSubject(title);
				new Mailer(tencentHost, tencentPort, tencentFromUser, tencentFromUserPassward, TransportStrategy.SMTP_SSL).sendMail(email);
			
			}
		});
	}
	
	
	/**
	 * 提交发布单时候发送给项目管理员
	 * @param applyId
	 */
	public static void sendSubmitMail(long applyId){
			logger.info("send mail!");
			ApplyService applyService = (ApplyService)SpringUtil.getBean("applyService");
			ApplyQuery aQuery = new ApplyQuery();
			aQuery.setId(applyId);;
			Result<Apply> rApply = applyService.get(aQuery);
			
			ProjectService projectService = (ProjectService)SpringUtil.getBean("projectService");
			ProjectQuery pQuery = new ProjectQuery();
			pQuery.setId(rApply.getValue().getProjectId());
			Result<Project> rProject = projectService.get(pQuery);
			
			ProjectUserService projectUserService = (ProjectUserService)SpringUtil.getBean("projectUserService");
			ProjectUserQuery puQuery = new ProjectUserQuery();
			puQuery.setProjectId(rProject.getValue().getId());
			puQuery.setType(ProjectConstants.PROJECT_USER_TYPE_ADMIN);
			Result<List<ProjectUser>> rUser = projectUserService.find(puQuery);

			UsersService usersService = (UsersService)SpringUtil.getBean("usersService");
			UsersQuery userQuery = new UsersQuery();
			userQuery.setId(rApply.getValue().getUserId());;
			Result<Users> ru = usersService.get(userQuery);
			
			String title = rProject.getValue().getTitle() + "-" + rApply.getValue().getTitle();
			StringBuffer content = new StringBuffer();
			content.append(title).append("\n\r<br>")
			.append(ru.getValue().getName()).append("(")
			.append(ru.getValue().getMobile()).append(")")
			.append("向你提交一个线上发布单，请尽快审批！").append("<br>")
			.append("https://publish.tools.jituancaiyun.com/publish/index.html");
			
			List<ProjectUser> users = rUser.getValue();
			List<String> mails = new ArrayList<String>();
			for (ProjectUser projectUser : users) {
				
				UsersQuery uQuery = new UsersQuery();
				uQuery.setId(projectUser.getUserId());;
				Result<Users> rUsers = usersService.get(uQuery);
				mails.add(rUsers.getValue().getMail());
			}
		try {
			sendTencentMail(title, content.toString(), mails);
			logger.info("send mail ok! to:" + mails.toString());
		} catch (Exception e) {
			try {
				MailServiceClient.send(mails, "internal_notice@shinemo.com", "发布通知", title, content.toString());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		}
		

	}
	
	/**
	 * 发布完成后通知全部管理员
	 * @param applyId
	 */
	public static void sendNotifyMail(long applyId){
		try {
			ApplyService applyService = (ApplyService)SpringUtil.getBean("applyService");
			ApplyQuery aQuery = new ApplyQuery();
			aQuery.setId(applyId);;
			Result<Apply> rApply = applyService.get(aQuery);
			
			ProjectService projectService = (ProjectService)SpringUtil.getBean("projectService");
			ProjectQuery pQuery = new ProjectQuery();
			pQuery.setId(rApply.getValue().getProjectId());
			Result<Project> rProject = projectService.get(pQuery);
			
			ProjectUserService projectUserService = (ProjectUserService)SpringUtil.getBean("projectUserService");
			ProjectUserQuery puQuery = new ProjectUserQuery();
			puQuery.setProjectId(rProject.getValue().getId());
			puQuery.setType(ProjectConstants.PROJECT_USER_TYPE_ADMIN);
			Result<List<ProjectUser>> rUser = projectUserService.find(puQuery);

			UsersService usersService = (UsersService)SpringUtil.getBean("usersService");

//			UsersQuery userAdminQuery = new UsersQuery();
//			userAdminQuery.setType(ProjectConstants.USER_TYPE_ADMIN);
//			Result<List<Users>> superAdmin = usersService.find(userAdminQuery);
//			
//			userAdminQuery = new UsersQuery();
//			userAdminQuery.setType(ProjectConstants.USER_TYPE_SUPERADMIN);
//			Result<List<Users>> admin = usersService.find(userAdminQuery);
//			
//			Map<Long,Users> userTmp = new HashMap<Long,Users>();
			
//			for (Users users : superAdmin.getValue()) {
//				userTmp.put(users.getId(), users);
//			}
//			for (Users users : admin.getValue()) {
//				userTmp.put(users.getId(), users);
//			}
			
			//谁操作的
			UsersQuery userQuery = new UsersQuery();
			userQuery.setId(rApply.getValue().getUserId());;
			Result<Users> ru = usersService.get(userQuery);
			
			Project p = rProject.getValue();
			Apply a = rApply.getValue();
			String title = p.getTitle() + "-" + a.getTitle();
			StringBuffer content = new StringBuffer();
			content
			.append(ru.getValue().getName()).append("(")
			.append(ru.getValue().getMobile()).append(")")
			.append("完成一个发布单！").append("<br>")
			.append("项目名：").append(p.getTitle()).append("<br>")
			.append("发布单：").append(a.getTitle()).append("<br>")
			.append("代码分支:").append(p.getGit()).append(":").append(a.getGitBranch()).append("<br>")
			.append("详情点击：")
			.append("https://publish.tools.jituancaiyun.com/publish/index.html");
			
			List<ProjectUser> usersList = rUser.getValue();
			List<String> mails = new ArrayList<String>();
			//mails.add("web@shinemo.com");
			for (ProjectUser projectUser : usersList) {
				
				UsersQuery uQuery = new UsersQuery();
				uQuery.setId(projectUser.getUserId());;
				Result<Users> rUsers = usersService.get(uQuery);
				mails.add(rUsers.getValue().getMail());
//				userTmp.put(rUsers.getValue().getId(), rUsers.getValue());
			}
//			
//			//组装邮件列表
//			for (Users users : userTmp.values()) {
//				mails.add(users.getMail());
//			}
			
			sendTencentMail(title, content.toString(), mails);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		List<String> tos = new ArrayList<String>();
		tos.add("luohj@shinemo.com");
		MailUtil.sendTencentMail("测试邮件", "测试，请忽略", tos);
	}
	
}
