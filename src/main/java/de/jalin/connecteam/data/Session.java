package de.jalin.connecteam.data;

import java.time.LocalDateTime;

public class Session {

	private Subscriber subscriber;
	private long id;
	private LocalDateTime validSince;
	private LocalDateTime validUntil;
	private String token;
	private boolean valid;

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

	public LocalDateTime getValidSince() {
		return validSince;
	}

	public void setValidSince(LocalDateTime validSince) {
		this.validSince = validSince;
	}

	public LocalDateTime getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(LocalDateTime validUntil) {
		this.validUntil = validUntil;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
