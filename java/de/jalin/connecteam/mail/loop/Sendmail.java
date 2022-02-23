package de.jalin.connecteam.mail.loop;

import javax.mail.MessagingException;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.Mailinglist;
import de.jalin.connecteam.mail.message.MailinglistMessage;

public class Sendmail {

	private static final Logger log = Logger.getLogger("Sendmail.class");
	
	public void send(Mailinglist ml, MailinglistMessage msg) {
		try {
			final SimpleEmail email = new SimpleEmail();
			email.setDebug(false);
			email.setHostName(ml.getSmtpHost());
			email.setSslSmtpPort("465");
//			email.setStartTLSRequired(true);
			email.setAuthentication(ml.getSmtpLogin(), ml.getSmtpPasswd());
			email.setSSLOnConnect(true);
			email.addTo(ml.getSubscribers());
			email.setFrom(ml.getEmailAddress(), ml.getName());
			email.setSubject(msg.getSubject());
//			email.setHtmlMsg(msg.getHtmlContent());
//			email.setTextMsg(msg.getTextContent());
			email.setMsg(msg.getTextContent());
//			email.buildMimeMessage();
			
//			email.setAuthenticator(new DefaultAuthenticator(ml.getSmtpLogin(), ml.getSmtpPasswd()));
			email.send();
		} catch (EmailException e) {
			log.error(e);
		}
	}
}
