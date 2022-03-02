package de.jalin.connecteam.data;

import java.time.LocalDateTime;

public class Message {

	private Topic topic;
	private long id;
	private String subject;
	private String sender;
	private String message;
	private String token;
	private LocalDateTime processing;

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getProcessing() {
		return processing;
	}

	public void setProcessing(LocalDateTime processing) {
		this.processing = processing;
	}
	
}
