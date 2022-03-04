package de.jalin.connecteam.mail.message;

public class AttachmentPath {

	private String contentType;
	private String name;
	private String filename;

	public String getContentType() {
		return contentType;
	}

	public String getName() {
		return name;
	}

	public String getFilename() {
		return filename;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setName(String attachmentName) {
		this.name = attachmentName;
	}

	public void setFilename(String attachmentFilename) {
		this.filename = attachmentFilename;
	}

}
