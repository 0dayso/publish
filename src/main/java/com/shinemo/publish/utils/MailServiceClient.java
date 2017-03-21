package com.shinemo.publish.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 *
 *  @Author Denny Ye
 *  @Date 2013-5-19
 *  @Version 1.0
 */
@Service("mailService")
public class MailServiceClient {
	private static final Logger LOG = LoggerFactory.getLogger(MailServiceClient.class);
	
	private static ExecutorService hanlderPool = Executors.newFixedThreadPool(10);
    //mail server
    private static String host = "smtp.shinemo.com";
   
    private static String username = "internal_notice@shinemo.com";
    private static String password = "ServiceShineMo20132";
    
	public static void send(String sendTo, String mailFrom, String personalName, 
			String subject, String mailBody) {
		hanlderPool.execute(new SendMail(sendTo, mailFrom, personalName, subject, mailBody));
	}

	
	public static void send(List<String> sendTos, String mailFrom, String personalName, 
			String subject, String mailBody) {
		hanlderPool.execute(new SendMail(sendTos, mailFrom, personalName, subject, mailBody));
	}
	
    /**
     */
    public static class EmailAutherticator extends Authenticator {
        public EmailAutherticator() {
            super();
        }

        public EmailAutherticator(String user, String pwd) {
            super();
            username = user;
            password = pwd;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
    
    static class SendMail implements Runnable {
    	
    	private String sendTo;
    	private List<String> sendTos;
    	private String mailFrom;
    	private String personalName;
    	private String subject;
    	private String mailBody;
    	
		public SendMail(String sendTo, String mailFrom, String personalName, 
				String subject, String mailBody) {
			this.sendTo = sendTo;
			this.mailFrom = mailFrom;
			this.personalName = personalName;
			this.subject = subject;
			this.mailBody = mailBody;
		}
		
		public SendMail(List<String> sendTos, String mailFrom, String personalName, 
				String subject, String mailBody) {
			this.sendTos = sendTos;
			this.mailFrom = mailFrom;
			this.personalName = personalName;
			this.subject = subject;
			this.mailBody = mailBody;
		}

		@Override
		public void run() {
			try {
				Properties props = new Properties();
				Authenticator auth = new EmailAutherticator();
				props.put("mail.smtp.host", host);
				props.put("mail.smtp.auth", "true");
				Session session = Session.getDefaultInstance(props, auth);
				
				MimeMessage message = new MimeMessage(session);
				
				message.setSubject(subject); 
				message.setText(mailBody,"GBK","html");
				//message.setText(text, charset, subtype);
				message.setSentDate(new Date());
				Address address = new InternetAddress(mailFrom, personalName);
				message.setFrom(address);
				if(!StringUtils.isEmpty(sendTo)){
					Address toAddress = new InternetAddress(sendTo);
					message.addRecipient(Message.RecipientType.TO, toAddress);
				}
				
				if(sendTos!=null){
					for (String sendMail : sendTos) {
						Address toAddress = new InternetAddress(sendMail);
						message.addRecipient(Message.RecipientType.TO, toAddress);
					}
				}
				
				Transport.send(message);
				
				LOG.warn("Send mail to " + sendTo + " successfully with subject " + subject);
			} catch (Exception ex) {
				LOG.warn("Failed to send mail to " + sendTo + ". cause:" + ex);
			}
			
		}
    }
    
    
    public static void main(String[] args) {
    	MailServiceClient mail = new MailServiceClient();
    	List<String> sendTos = new ArrayList<String>();
    	sendTos.add("luohj1980@sina.com");
    	sendTos.add("luohj@shinemo.com");
    	MailServiceClient.send(sendTos, "internal_notice@shinemo.com", "mydb", "DB审核", "提交了一个DB需求，需要您审批.\n详情点击：\nhttps://mydb.admin.jituancaiyun.com/mydb/home.htm");
	}
}
