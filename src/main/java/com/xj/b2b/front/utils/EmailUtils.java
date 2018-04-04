package com.xj.b2b.front.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.xj.b2b.back.utils.DictUtils;
import com.xj.common.utils.StringUtils;

/**
 * Email Utils
 * 
 * @version yangb修订于20151229
 */
public class EmailUtils {

	/**
	 * 发送邮件，请先使用 EmailUtils.validateEmail(receiveUser) 验证邮箱地址
	 * @param subject 标题
	 * @param sendHtml 正文
	 * @param receiveUser 收件人邮箱地址
	 * @throws Exception
	 */
	public static void sendMail(String subject, String sendHtml, String receiveUser) throws Exception {

		// 获取 email 参数
		String dictType = "sender_email";
		String mailAddr = DictUtils.getDictValue("addr", dictType, "");
		String mailSmtp = DictUtils.getDictValue("smtp", dictType, "");
		String mailUser = DictUtils.getDictValue("user", dictType, "");
		String mailPwd = DictUtils.getDictValue("pwd", dictType, "");

		Properties prop = new Properties();
		prop.setProperty("mail.host", mailSmtp);
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.auth", "true");

		// 1、创建session
		Session session = Session.getInstance(prop);
		// 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
		session.setDebug(true);
		// 2、通过session得到transport对象
		Transport ts = session.getTransport();
		// 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
		ts.connect(mailSmtp, mailUser, mailPwd);
		// 4、创建邮件

		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress(mailAddr));

		// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveUser));
		// 邮件的标题
		message.setSubject(subject);
		// 邮件的html文本内容
		message.setContent(sendHtml, "text/html;charset=UTF-8");

		// 5、发送邮件
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
	}

	/**
	 * 验证邮箱地址是否有效
	 */
	public static boolean validateEmail(String email) {
		boolean flag = false;
		if (StringUtils.isBlank(email)) {
			return flag;
		}
		try {
			String check = "[\\w[.-]]+@[\\w[.-]]+\\.[\\w]+";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

}
