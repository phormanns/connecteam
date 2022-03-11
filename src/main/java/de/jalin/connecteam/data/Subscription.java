package de.jalin.connecteam.data;

import java.time.LocalDateTime;

public class Subscription {

	private Topic topic;
	private Subscriber subscriber;
	private long id;
	private LocalDateTime subscribeDate;
	private LocalDateTime unsubscribeDate;
	private boolean recievesDigest = false;
	private boolean recievesMessages = false;
	private boolean recievesModeration = false;
	private boolean maySendMessages = false;
	private boolean active = false;

	public Subscription() {
		id = 0L;
		topic = null;
		subscriber = null;
	}
	
	public Topic getTopic() {
		return topic;
	}
	
	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDateTime getSubscribeDate() {
		return subscribeDate;
	}

	public void setSubscribeDate(LocalDateTime subscribeDate) {
		this.subscribeDate = subscribeDate;
		final LocalDateTime now = LocalDateTime.now();
		active = subscribeDate.isBefore(now) && unsubscribeDate != null && now.isBefore(unsubscribeDate);
	}

	public LocalDateTime getUnsubscribeDate() {
		return unsubscribeDate;
	}

	public void setUnsubscribeDate(LocalDateTime unsubscribeDate) {
		this.unsubscribeDate = unsubscribeDate;
		final LocalDateTime now = LocalDateTime.now();
		active = now.isBefore(unsubscribeDate) && subscribeDate != null && subscribeDate.isBefore(now);
	}

	public boolean recievesDigest() {
		return recievesDigest;
	}

	public void setRecievesDigest(boolean recievesDigest) {
		this.recievesDigest = recievesDigest;
	}

	public boolean recievesMessages() {
		return recievesMessages;
	}

	public void setRecievesMessages(boolean recievesMessages) {
		this.recievesMessages = recievesMessages;
	}

	public boolean recievesModeration() {
		return recievesModeration;
	}

	public void setRecievesModeration(boolean recievesModeration) {
		this.recievesModeration = recievesModeration;
	}

	public boolean maySendMessages() {
		return maySendMessages;
	}

	public void setMaySendMessages(boolean maySendMessages) {
		this.maySendMessages = maySendMessages;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
