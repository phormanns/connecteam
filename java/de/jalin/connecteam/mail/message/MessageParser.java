package de.jalin.connecteam.mail.message;

import java.io.IOException;

import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.Mailinglist;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MessageParser {

	private final static Logger log = Logger.getLogger("MessageTransformer.class");
	
	private final Mailinglist mailinglist;

	public MessageParser(Mailinglist ml) {
		this.mailinglist = ml;
	}

	public MailinglistMessage parseMessage(Message message) throws MessagingException, IOException {
		final MailinglistMessage email = new MailinglistMessage();
		final String subject = message.getSubject();
		final Address[] fromAddresses = message.getFrom();
		final Address[] recipientAddresses = message.getAllRecipients();
		final Address from = fromAddresses[0];
		email.setFromAddress(mailinglist.getName() + " <" + mailinglist.getEmailAddress() + ">");
		if (from instanceof InternetAddress) {
			final InternetAddress iFrom = (InternetAddress) from;
			final String personal = iFrom.getPersonal();
			email.setFromAddress(personal + " <" + mailinglist.getEmailAddress() + ">");
		}
		email.setSubject(subject);
		email.setToAddress(mailinglist.getName() + " <" + mailinglist.getEmailAddress() + ">");
		if (message instanceof MimeMessage) {
			final MimeMessage mimeMessage = (MimeMessage) message;
			final Object contentObject = mimeMessage.getContent();
			if (contentObject instanceof String) {
				email.setTextContent((String) contentObject);
			}
			if (contentObject instanceof Multipart) {
				final Multipart multipart = (Multipart) contentObject;
				extractParts(multipart);
			}
		}
		return email;
	}

	public void extractParts(final Multipart multipart) throws MessagingException, IOException {
		for (int idx = 0; idx < multipart.getCount(); idx++) {
			final BodyPart part = multipart.getBodyPart(idx);
			log.info("ContentType: " + part.getContentType());
			log.info("Description: " + part.getDescription());
			log.info("FileName: " + part.getFileName());
			log.info("Disposition: " + part.getDisposition());
			if (part.getContentType() != null && part.getContentType().startsWith("multipart")) {
				final Object contentObject = part.getContent();
				if (contentObject instanceof Multipart) {
					final Multipart innerPart = (Multipart) contentObject;
					extractParts(innerPart);
				}
			}
		}
	}

}
