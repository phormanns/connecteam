package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
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
import de.jalin.connecteam.mail.message.Post;

public class Sendmail {

	private static final Logger log = Logger.getLogger("Sendmail.class");
	
	private final Topic topic;
	private final RandomIdent random;
	
	public Sendmail(final Topic topic) {
		this.topic = topic;
	    this.random = new RandomIdent();
	}
	
	public void sendPost(final Post listPost) {
		if (Post.POST_ACCEPTED == listPost.getStatus()) {
			topic.getSubscriptions().forEach(new Consumer<Subscription>() {
				@Override
				public void accept(final Subscription subscription) {
					if (subscription.isActive() && subscription.recievesMessages()) {
						final String subscriberAddress = subscription.getSubscriber().getAddress();
						try {
							smtpSend(listPost, subscriberAddress, false);
						} catch (CxException e) {
							log.error(e);
						}
					}
				}
			});
		}
		if (Post.POST_NEEDS_APPROVAL == listPost.getStatus()) {
			topic.getSubscriptions().forEach(new Consumer<Subscription>() {
				@Override
				public void accept(final Subscription subscription) {
					if (subscription.isActive() && subscription.recievesModeration()) {
						final String subscriberAddress = subscription.getSubscriber().getAddress();
						try {
							smtpSend(listPost, subscriberAddress, true);
						} catch (CxException e) {
							log.error(e);
						}
					}
				}
			});
		}
	}

	public void sendAll(final List<Post> sendQueue) {
		sendQueue.forEach(new Consumer<Post>() {
			@Override
			public void accept(Post mlPost) {
				sendPost(mlPost);
			}
		});
	}

	private void smtpSend(final Post mlPost, final String toAddress, final boolean needsApproval) throws CxException {
        try {
        	log.info("sending to " + toAddress);
    		final String fromAddress = topic.getAddress();
    		final String subject = mlPost.getSubject();
    		final String content = mlPost.getTextContent();
    		final String originalFrom = mlPost.getOriginalFrom();
			final AuthenticatingSMTPClient client = handleSMTPDialog(fromAddress, toAddress);
			final Writer messageWriter = client.sendMessageData();
			if (messageWriter == null) {
				log.error("error_sending_email_relay");
			    throw new CxException("error_sending_email_relay");
			}
			try (final PrintWriter printWriter = new PrintWriter(messageWriter)) {
			    printWriter.write(makeSMTPHeaders(fromAddress, toAddress, subject));
			    
			    if (needsApproval) {
				    printWriter.write(makeApprovalInfo(fromAddress, originalFrom, mlPost));
			    } else {
				    printWriter.write(makeTopicInfo(fromAddress, originalFrom));
			    }
			    printWriter.write(content);
			    printWriter.write(makeAttachmentsList(mlPost));
			}
			if (!client.completePendingCommand()) {
			    throw new CxException("error_sending_email");
			}
			client.logout();
			client.disconnect();
		} catch (NumberFormatException | IOException | GeneralSecurityException e) {
			throw new CxException(e);
		}
    }

	public String makeApprovalInfo(final String fromAddress, final String originalFrom, final Post mlPost) {
		final StringBuffer messageInfo = new StringBuffer("Nachricht von " + originalFrom + "\n");
		messageInfo.append("an den Verteiler " + fromAddress + "\n");
		messageInfo.append("wartet auf Deine Freigabe:\n");
		messageInfo.append(buildWebdomainPart() + "/mod/" + mlPost.getRandom() + " \n\n");
		final String info = messageInfo.toString();
		return info;
	}

	public String makeTopicInfo(final String fromAddress, final String originalFrom) {
		final StringBuffer messageInfo = new StringBuffer("Nachricht von " + originalFrom + "\n");
		messageInfo.append("an den Verteiler " + fromAddress + "\n\n");
		final String info = messageInfo.toString();
		return info;
	}

	private AuthenticatingSMTPClient handleSMTPDialog(final String fromAddress, final String toAddress)
			throws UnknownHostException, SocketException, IOException, CxException, NoSuchAlgorithmException,
			InvalidKeyException, InvalidKeySpecException {
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
		return client;
	}

	private String makeAttachmentsList(final Post mlPost) {
		final StringWriter writer = new StringWriter();
		writer.write("\n\nAnlagen:\n");
		final Collection<AttachmentPath> attachments = mlPost.getAttachments();
		for (AttachmentPath att : attachments) {
			writer.write(att.getName() + " " + buildWebdomainPart() + "/att/" + mlPost.getRandom() + "/" + att.getFilename() + "\n");
		}
		return writer.toString();
	}

	private String buildWebdomainPart() {
		String webDomain = topic.getWebDomain();
		String webDomainWithProtocol = "https://" + webDomain;
		if (webDomain.startsWith("localhost:")) {
			webDomainWithProtocol = "http://" + webDomain;
		}
		return webDomainWithProtocol;
	}

	private String makeSMTPHeaders(final String fromAddress, final String toAddress, final String subject) {
		final SimpleSMTPHeader header = new SimpleSMTPHeader(fromAddress, toAddress, subject);
		header.addHeaderField("Content-Type", "text/plain; charset=ISO-8859-1");
		header.addHeaderField("Content-Transfer-Encoding", "8bit");
		header.addHeaderField("Message-ID", "<" + random.nextIdent() + "-" + fromAddress + ">");
		return header.toString();
	}
	
}
