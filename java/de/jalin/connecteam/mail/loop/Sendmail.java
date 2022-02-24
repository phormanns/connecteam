package de.jalin.connecteam.mail.loop;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient.AUTH_METHOD;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.Mailinglist;
import de.jalin.connecteam.mail.message.MailinglistMessage;

public class Sendmail {

	private static final Logger log = Logger.getLogger("Sendmail.class");
	
	private final Mailinglist mailinglist;
	private Session mailSession;
	private Transport transport;
	
	public Sendmail(final Mailinglist ml) {
		this.mailinglist = ml;
		final Properties props = new Properties();
		props.setProperty("mail.debug", "true");
		props.setProperty("mail.host", ml.getSmtpHost());
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", ml.getSmtpHost());
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
 		mailSession = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ml.getSmtpLogin(), ml.getSmtpPasswd());
			}
		});
//		try {
//			transport  = mailSession.getTransport("smtp");
//			transport.connect(mailinglist.getSmtpHost(), 25, ml.getSmtpLogin(), ml.getSmtpPasswd());
//		} catch (MessagingException e) {
//			log.error(e);
//		}
	}
	
	public void send(MailinglistMessage msg) {
		try {
			smtpSend(mailinglist.getSmtpHost(), "587", mailinglist.getEmailAddress(), msg.getToAddress(), msg.getSubject(), msg.getHtmlContent());
//			final MimeMessage message = new MimeMessage(mailSession);
//			message.setFrom(new InternetAddress(mailinglist.getEmailAddress()));
//			message.setRecipient(RecipientType.TO, new InternetAddress(mailinglist.getSubscribers()[0]));
//			message.setSubject(msg.getSubject(), "UTF-8");
//			final Multipart multi = new MimeMultipart();
//			final MimeBodyPart part = new MimeBodyPart();
//			part.setContent("<p>hello world</p>\n\n", "text/html;charset=utf-8");
////			part.setContent(msg.getHtmlContent(), "text/html;charset=utf-8");
//			multi.addBodyPart(part);
//			message.setContent(multi);
//			message.setSentDate(new Date());
//			Transport.send(message);
		} catch (CxException e) {
			log.error(e);
		}
	}

    private void smtpSend(final String smtpHost, final String smtpPort, final String fromAddress, final String toAddress, final String subject, final String text) throws CxException {
        try {
			final AuthenticatingSMTPClient client = new AuthenticatingSMTPClient();
			final String canonicalHostName = InetAddress.getLocalHost().getHostName();
			client.connect(smtpHost, Integer.parseInt(smtpPort));
			int reply = client.getReplyCode();
			if (!SMTPReply.isPositiveCompletion(reply)) {
			    throw new CxException("error_sending_email_server");
			}
			boolean execTLS = client.execTLS();
			log.info("StartTLS: " + execTLS);
			boolean elogin = client.elogin(canonicalHostName);
			log.info("EHLO: " + elogin);
			boolean authOk = client.auth(AUTH_METHOD.LOGIN, mailinglist.getSmtpLogin(), mailinglist.getSmtpPasswd());
			log.info("Authentication: " + authOk);
			client.setSender(fromAddress.trim());
			client.addRecipient(toAddress.trim());
			final Writer sendMessageData = client.sendMessageData();
			if (sendMessageData == null) {
			    throw new CxException("error_sending_email_relay");
			}
			try (PrintWriter wr = new PrintWriter(sendMessageData)) {
			    final SimpleSMTPHeader header = new SimpleSMTPHeader(fromAddress, toAddress, subject);
			    header.addHeaderField("Content-Type", "text/html; charset=ISO-8859-1");
			    header.addHeaderField("Content-Transfer-Encoding", "8bit");
			    wr.write(header.toString());
			    wr.write(text);
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
	
	public void close() {
//		try {
//			transport.close();
//		} catch (MessagingException e) {
//			log.error(e);
//		}
	}
}
