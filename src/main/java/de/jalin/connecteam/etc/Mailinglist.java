package de.jalin.connecteam.etc;

public class Mailinglist {
	
	private String emailAddress;
	private String name;
	private String description;
	private String[] subscribers;
	
	private String imapHost;
	private String imapLogin;
	private String imapPasswd;
	private boolean imapStartTLS;

	private String smtpHost;
	private String smtpLogin;
	private String smtpPasswd;
	private boolean smtpStartTLS;
	
	public Mailinglist() { }
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImapHost() {
		return imapHost;
	}

	public void setImapHost(String imapHost) {
		this.imapHost = imapHost;
	}

	public String getImapLogin() {
		return imapLogin;
	}

	public void setImapLogin(String imapLogin) {
		this.imapLogin = imapLogin;
	}

	public String getImapPasswd() {
		return imapPasswd;
	}

	public void setImapPasswd(String imapPasswd) {
		this.imapPasswd = imapPasswd;
	}

	public boolean isImapStartTLS() {
		return imapStartTLS;
	}

	public void setImapStartTLS(boolean imapStartTLS) {
		this.imapStartTLS = imapStartTLS;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpLogin() {
		return smtpLogin;
	}

	public void setSmtpLogin(String smtpLogin) {
		this.smtpLogin = smtpLogin;
	}

	public String getSmtpPasswd() {
		return smtpPasswd;
	}

	public void setSmtpPasswd(String smtpPasswd) {
		this.smtpPasswd = smtpPasswd;
	}

	public boolean isSmtpStartTLS() {
		return smtpStartTLS;
	}

	public void setSmtpStartTLS(boolean smtpStartTLS) {
		this.smtpStartTLS = smtpStartTLS;
	}

	public String[] getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(String[] subscribers) {
		this.subscribers = subscribers;
	}

	
}
