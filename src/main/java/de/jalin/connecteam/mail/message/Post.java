package de.jalin.connecteam.mail.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.jalin.connecteam.etc.RandomIdent;

public class Post {

	public final static int POST_REJECTED = -1;
	public final static int POST_ACCEPTED = 0;
	public final static int POST_NEEDS_APPROVAL = 1;
	public final static int POST_ON_HOLD = 2;
	
	private final String random;
	private String subject;
	private String fromAddress;
	private String originalFrom;
	private String toAddress;
	private String textContent;
	private int status;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void attach(AttachmentPath attachment) {
		attachments.put(attachment.getName(), attachment);
	}
	
	public Collection<AttachmentPath> getAttachments() {
		return attachments.values();
	}
}
