package de.jalin.connecteam.mail.message;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.Mailinglist;

public class MessageParser {

	private final static Logger log = Logger.getLogger("MessageTransformer.class");
	
	private final Mailinglist mailinglist;

	public MessageParser(Mailinglist ml) {
		this.mailinglist = ml;
	}

	public MailinglistMessage parseMessage(Message message) throws CxException {
		final MailinglistMessage email = new MailinglistMessage();
		if (message instanceof MimeMessage) {
			final MimeMessageParser parser = new MimeMessageParser((MimeMessage) message);
			try {
				parser.parse();
				email.setSubject(parser.getSubject());
				email.setTextContent(parser.getPlainContent());
				final String htmlContent = parser.getHtmlContent();
				if (htmlContent != null && !htmlContent.isEmpty()) {
					email.setHtmlContent(Jsoup.clean(htmlContent, Safelist.basic()));
				}
				email.setFromAddress(parser.getFrom());
				email.setToAddress(parser.getTo().get(0).toString());
				log.info("Subject: " + email.getSubject());
				log.info("From: " + email.getFromAddress());
				log.info("To: " + email.getToAddress());
			} catch (Exception e) {
				log.error(e);
				throw new CxException(e);
			}
		}
		return email;
	}

}
