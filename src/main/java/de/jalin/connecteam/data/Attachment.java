package de.jalin.connecteam.data;

public class Attachment {

	private Message message;
	private long id;
	private String filename;
	private String pathToken;

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPathToken() {
		return pathToken;
	}

	public void setPathToken(String pathToken) {
		this.pathToken = pathToken;
	}
}
