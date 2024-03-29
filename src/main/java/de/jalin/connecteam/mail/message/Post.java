package de.jalin.connecteam.mail.message;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.RandomIdent;

public class Post {

	public final static int POST_REJECTED = -1;
	public final static int POST_ACCEPTED = 0;
	public final static int POST_NEEDS_APPROVAL = 1;
	public final static int POST_ON_HOLD = 2;
	
	private long id;
	private String random;
	private String subject;
	private String fromAddress;
	private String originalFrom;
	private String toAddress;
	private String textContent;
	private LocalDateTime processingTime;
	private int status;
	private LocalDateTime statusUpdateTime;
	private Map<String, AttachmentPath> attachments;
	private Topic topic;
	
	public Post() {
		final RandomIdent randomIdent = new RandomIdent();
		random = randomIdent.nextIdent();
		attachments = new HashMap<>();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
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

	public LocalDateTime getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(LocalDateTime processingTime) {
		this.processingTime = processingTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public LocalDateTime getStatusUpdateTime() {
		return statusUpdateTime;
	}

	public void setStatusUpdateTime(LocalDateTime statusUpdateTime) {
		this.statusUpdateTime = statusUpdateTime;
	}

	public void attach(AttachmentPath attachment) {
		attachments.put(attachment.getName(), attachment);
	}
	
	public Collection<AttachmentPath> getAttachments() {
		return attachments.values();
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}
}
