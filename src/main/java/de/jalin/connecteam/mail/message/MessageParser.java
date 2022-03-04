package de.jalin.connecteam.mail.message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;

import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.DataDir;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.RandomIdent;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;

public class MessageParser {

	private final static Logger log = Logger.getLogger("MessageTransformer.class");
	
	private final Topic mailinglist;
	private final RandomIdent randomIdent;
	private final DataDir datadir;

	public MessageParser(final Topic mailinglist, final DataDir datadir) {
		this.mailinglist = mailinglist;
		this.randomIdent = new RandomIdent();
		this.datadir = datadir;
	}

	public MailinglistMessage parse(Message message) throws CxException {
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
				email.setOriginalFrom(from);
				email.setFromAddress(patchSenderAddress(from, listAddress));
				email.setToAddress(mailinglist.getName() + " <" + listAddress + ">");
				final List<DataSource> attachmentList = parser.getAttachmentList();
				if (attachmentList != null && !attachmentList.isEmpty()) {
					final String directoryName = datadir.getPath() + "/" + email.getRandom();
					final File directory = new File(directoryName);
					directory.mkdirs();
					for (final DataSource dataSource : attachmentList) {
						final String contentType = dataSource.getContentType();
						final String attachmentName = dataSource.getName();
						final String attachmentFilename = randomIdent.nextIdent() + ".att";
						final InputStream inputStream = dataSource.getInputStream();
						final FileOutputStream outputStream = new FileOutputStream(directory + "/" + attachmentFilename);
						copyStream(inputStream, outputStream);
						final AttachmentPath attachmentPath = new AttachmentPath();
						attachmentPath.setContentType(contentType);
						attachmentPath.setName(attachmentName);
						attachmentPath.setFilename(attachmentFilename);
						email.attach(attachmentPath);
					}
				}
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
	
	private void copyStream(InputStream source, OutputStream dest) throws IOException {
	    byte[] buf = new byte[4096];
	    int length;
	    while ((length = source.read(buf)) > 0) {
	        dest.write(buf, 0, length);
	    }
	}
}
