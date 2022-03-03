package de.jalin.connecteam.data;

import java.time.LocalDateTime;

public class Subscription {

	private Topic topic;
	private Subscriber subscriber;
	private long id;
	private LocalDateTime subscribeDate;
	private LocalDateTime unsubscribeDate;
	private boolean digest = false;
	private boolean moderator = false;
	private boolean active = false;

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
