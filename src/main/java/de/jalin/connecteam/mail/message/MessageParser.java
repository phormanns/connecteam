package de.jalin.connecteam.mail.message;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;

import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;

public class MessageParser {

	private final static Logger log = Logger.getLogger("MessageTransformer.class");
	
	private final Topic mailinglist;

	public MessageParser(Topic mailinglist) {
		this.mailinglist = mailinglist;
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
					final Document dirty = Jsoup.parse(htmlContent);
					final Cleaner cleaner = new Cleaner(Safelist.basic());
			        final Document clean = cleaner.clean(dirty);
			        final Html2PlainText html2PlainText = new Html2PlainText();
			        final String plainText = html2PlainText.getPlainText(clean);
			        email.setTextContent(plainText);
				}
				final String listAddress = mailinglist.getAddress();
				String from = parser.getFrom();
				email.setFromAddress(patchSenderAddress(from, listAddress));
				email.setToAddress(mailinglist.getName() + " <" + listAddress + ">");
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

	public String patchSenderAddress(String senderAddress, String mlAddress) {
		return senderAddress.replaceFirst("<[a-zA-Z0-9_\\.\\-\\+]+@[a-zA-Z0-9_\\.\\-]+>", "<" + mlAddress + ">");
	}
	
}
