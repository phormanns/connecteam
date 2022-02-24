package de.jalin.connecteam.mail.message;

public class MailinglistMessage {

	private String subject;
	private String fromAddress;
	private String toAddress;
	private String htmlContent;
	private String textContent;
	
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

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getHtmlContent() {
		if (htmlContent == null || htmlContent.isEmpty()) {
			final HtmlHelper helper = new HtmlHelper();
			return helper.getFormattedText(textContent);
		}
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	
}
