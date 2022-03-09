package de.jalin.connecteam.mail.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.jalin.connecteam.etc.RandomIdent;

public class Post {

	private final String random;
	private String subject;
	private String fromAddress;
	private String originalFrom;
	private String toAddress;
	private String textContent;
	private Map<String, AttachmentPath> attachments;
	
	
	public Post() {
		final RandomIdent randomIdent = new RandomIdent();
		random = randomIdent.nextIdent();
		attachments = new HashMap<>();
	}
	
	public String getRandom() {
		return random;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getOriginalFrom() {
		return originalFrom;
	}

	public void setOriginalFrom(String originalFrom) {
		this.originalFrom = originalFrom;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public void attach(AttachmentPath attachment) {
		attachments.put(attachment.getName(), attachment);
	}
	
	public Collection<AttachmentPath> getAttachments() {
		return attachments.values();
	}
}
