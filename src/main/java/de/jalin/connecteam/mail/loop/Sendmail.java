package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient.AUTH_METHOD;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

import de.jalin.connecteam.data.Subscription;
import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.RandomIdent;
import de.jalin.connecteam.mail.MailAccount;
import de.jalin.connecteam.mail.message.AttachmentPath;
import de.jalin.connecteam.mail.message.MailinglistMessage;

public class Sendmail {

	private static final Logger log = Logger.getLogger("Sendmail.class");
	
	private final Topic topic;
	private final RandomIdent random;
	
	public Sendmail(final Topic topic) {
		this.topic = topic;
	    this.random = new RandomIdent();
	}
	
	public void send(final MailinglistMessage msg) {
		topic.getSubscriptions().forEach(new Consumer<Subscription>() {
			@Override
			public void accept(final Subscription subscription) {
				if (subscription.isActive()) {
					final String subscriberAddress = subscription.getSubscriber().getAddress();
					try {
						smtpSend(msg, subscriberAddress);
					} catch (CxException e) {
						log.error(e);
					}
				}
			}
		});
	}

	public void sendAll(final List<MailinglistMessage> sendQueue) {
		sendQueue.forEach(new Consumer<MailinglistMessage>() {
			@Override
			public void accept(MailinglistMessage msg) {
				send(msg);
			}
		});
	
	}

	private void smtpSend(final MailinglistMessage msg, final String toAddress) throws CxException {
        try {
        	log.info("sending to " + toAddress);
    		final String topicAddress = topic.getAddress();
    		final String subject = msg.getSubject();
    		final String content = msg.getTextContent();
    		final String originalFrom = msg.getOriginalFrom();
			final MailAccount smtpAccount = topic.getSmtpAccount();
			final String smtpHost = smtpAccount.getHost();
			final int smtpPort = smtpAccount.getPort();
			final String smtpLogin = smtpAccount.getLogin();
			final String smtpPasswd = smtpAccount.getPasswd();
			final AuthenticatingSMTPClient client = new AuthenticatingSMTPClient();
			final String canonicalHostName = InetAddress.getLocalHost().getHostName();
			client.connect(smtpHost, smtpPort);
			int reply = client.getReplyCode();
			if (!SMTPReply.isPositiveCompletion(reply)) {
				log.error("smtp connect failed: " + smtpHost + ":" + smtpPort);
			    throw new CxException("error_smtp_client_connect");
			}
			if (!client.execTLS()) {
				log.error("smtp starttls failed: " + smtpHost + ":" + smtpPort);
			    throw new CxException("error_smtp_client_starttls");
			}
			if (!client.elogin(canonicalHostName)) {
				log.error("smtp ehlo failed: " + smtpHost + ":" + smtpPort);
			    throw new CxException("error_smtp_client_ehlo");
			}
			if (!client.auth(AUTH_METHOD.LOGIN, smtpLogin, smtpPasswd)) {
				log.error("smtp login failed: " + smtpLogin + "@" + smtpHost + ":" + smtpPort);
			    throw new CxException("error_smtp_client_login");
			}
			client.setSender(topicAddress.trim());
			client.addRecipient(toAddress.trim());
			final Writer messageWriter = client.sendMessageData();
			if (messageWriter == null) {
			    throw new CxException("error_sending_email_relay");
			}
			try (final PrintWriter printWriter = new PrintWriter(messageWriter)) {
			    final SimpleSMTPHeader header = new SimpleSMTPHeader(topicAddress, toAddress, subject);
			    header.addHeaderField("Content-Type", "text/plain; charset=ISO-8859-1");
			    header.addHeaderField("Content-Transfer-Encoding", "8bit");
			    header.addHeaderField("Message-ID", "<" + random.nextIdent() + "-" + topicAddress + ">");
			    printWriter.write(header.toString());
			    printWriter.write("Nachricht von " + originalFrom + "\n");
			    printWriter.write("an den Verteiler " + topicAddress + "\n\n");
			    printWriter.write(content);
			    printWriter.write("\n\nAnlagen:\n");
			    final Collection<AttachmentPath> attachments = msg.getAttachments();
			    for (AttachmentPath att : attachments) {
			    	printWriter.write(att.getName() + " http://localhost:8080/att/" + msg.getRandom() + "/" + att.getFilename() + "\n");
			    }
			}
			if (!client.completePendingCommand()) {
			    throw new CxException("error_sending_email");
			}
			client.logout();
			client.disconnect();
		} catch (NumberFormatException | IOException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new CxException(e);
		}
    }
	
}
