package de.jalin.connecteam.mail.message;

import de.jalin.connecteam.etc.Mailinglist;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

public class MessageTransformer {

	private final Mailinglist mailinglist;

	public MessageTransformer(Mailinglist ml) {
		this.mailinglist = ml;
	}

	public MailinglistMessage parseMessage(Message message) throws MessagingException {
		MailinglistMessage email = new MailinglistMessage();
		String subject = message.getSubject();
		final Address[] fromAddresses = message.getFrom();
		final Address[] recipientAddresses = message.getAllRecipients();
		final Address from = fromAddresses[0];
		email.setFromAddress(mailinglist.getName() + " <" + mailinglist.getEmailAddress() + ">");
		if (from instanceof InternetAddress) {
			InternetAddress iFrom = (InternetAddress) from;
			String personal = iFrom.getPersonal();
			email.setFromAddress(personal + " <" + mailinglist.getEmailAddress() + ">");
		}
		email.setSubject(subject);
		email.setToAddress(mailinglist.getName() + " <" + mailinglist.getEmailAddress() + ">");
		return email;
	}

}
