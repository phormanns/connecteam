package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient.AUTH_METHOD;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.Mailinglist;
import de.jalin.connecteam.mail.message.MailinglistMessage;

public class Sendmail {

	private static final Logger log = Logger.getLogger("Sendmail.class");
	
	private final Mailinglist mailinglist;
	
	public Sendmail(final Mailinglist ml) {
		this.mailinglist = ml;
	}
	
	public void send(MailinglistMessage msg) {
		try {
			for (String subs : mailinglist.getSubscribers()) {
				smtpSend(mailinglist.getSmtpHost(), "587", mailinglist.getEmailAddress(), subs, msg.getSubject(), msg.getHtmlContent());
			}
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
	
}
