package io.microvibe.util.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.mail.Email;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;

public class EmailUtil {
	private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

	private EmailUtil() {
	}

	static final int HTML_TYPE = 1;
	static final int TEXT_TYPE = 0;

	public static boolean sendTextMail(String host, String user, String passwd, String subject,
			String text, String... to) {
		return sendMail(TEXT_TYPE, host, user, passwd, subject, text, to);
	}

	public static boolean sendHtmlMail(String host, String user, String passwd, String subject,
			String html, String... to) {
		return sendMail(HTML_TYPE, host, user, passwd, subject, html, to);
	}

	@SuppressWarnings("rawtypes")
	private static boolean sendMail(int messageType, String host, String user, String passwd, String subject,
			String message, String... to) {
		try {
			Email mail = Email.create()
					.from(user).to(to)
					.subject(subject);
			switch (messageType) {
			case HTML_TYPE:
				mail.addHtml(message);
				break;
			case TEXT_TYPE:
			default:
				mail.addText(message);
				break;
			}
			SmtpServer smtpServer = SmtpServer.create(host).authenticateWith(user, passwd);
			SendMailSession session = smtpServer.createSession();
			session.open();
			session.sendMail(mail);
			session.close();
			return true;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return false;
		}
	}

}
