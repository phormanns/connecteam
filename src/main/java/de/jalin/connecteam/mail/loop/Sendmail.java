package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
import de.jalin.connecteam.mail.message.MailinglistMessage;

public class Sendmail {

	private static final Logger log = Logger.getLogger("Sendmail.class");
	
	private final Topic topic;
	private final RandomIdent random;
	
	public Sendmail(final Topic topic) {
		this.topic = topic;
	    this.random = new RandomIdent();
	}
	
	public void send(MailinglistMessage msg) {
		final String topicAddress = topic.getAddress();
		final String subject = msg.getSubject();
		final String content = msg.getTextContent();
		topic.getSubscriptions().forEach(new Consumer<Subscription>() {
			@Override
			public void accept(Subscription subscription) {
				if (subscription.isActive()) {
					final String subscriberAddress = subscription.getSubscriber().getAddress();
					try {
						smtpSend(topicAddress, subscriberAddress, subject, content);
					} catch (CxException e) {
						log.error(e);
					}
				}
			}
		});
	}

    private void smtpSend(final String fromAddress, final String toAddress, final String subject, final String text) throws CxException {
        try {
        	log.info("sending to " + toAddress);
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
			client.setSender(fromAddress.trim());
			client.addRecipient(toAddress.trim());
			final Writer messageWriter = client.sendMessageData();
			if (messageWriter == null) {
			    throw new CxException("error_sending_email_relay");
			}
			try (final PrintWriter printWriter = new PrintWriter(messageWriter)) {
			    final SimpleSMTPHeader header = new SimpleSMTPHeader(fromAddress, toAddress, subject);
			    header.addHeaderField("Content-Type", "text/plain; charset=ISO-8859-1");
			    header.addHeaderField("Content-Transfer-Encoding", "8bit");
			    header.addHeaderField("Message-ID", "<" + random.nextIdent() + "-" + fromAddress + ">");
			    printWriter.write(header.toString());
			    printWriter.write(text);
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
