package de.jalin.connecteam.data;

import java.time.LocalDateTime;

public class Subscription {

	private Topic topic;
	private Subscriber subscriber;
	private long id;
	private LocalDateTime subscribeDate;
	private LocalDateTime unsubscribeDate;
	private boolean digest;
	private boolean moderator;
	private boolean active;

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
	}

	public LocalDateTime getUnsubscribeDate() {
		return unsubscribeDate;
	}

	public void setUnsubscribeDate(LocalDateTime unsubscribeDate) {
		this.unsubscribeDate = unsubscribeDate;
	}

	public boolean isDigest() {
		return digest;
	}

	public void setDigest(boolean digest) {
		this.digest = digest;
	}

	public boolean isModerator() {
		return moderator;
	}

	public void setModerator(boolean moderator) {
		this.moderator = moderator;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
