package de.jalin.connecteam.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.jalin.connecteam.mail.MailAccount;

public class Topic {

	private Workspace workspace;
	private long id;
	private String address;
	private String name;
	private String description;
	private String webDomain;
	private final Map<String,Subscription> subscriptions;
	private MailAccount imapAccount;
	private MailAccount smtpAccount;

	public Topic() {
		subscriptions = new HashMap<>();
	}
	
	public Workspace getWorkspace() {
		return workspace;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
	
	public String getWebDomain() {
		return webDomain;
	}

	public void setWebDomain(String webDomain) {
		this.webDomain = webDomain;
	}

	public void add(Subscription subscription) {
		subscriptions.put(subscription.getSubscriber().getAddress(), subscription);
	}
	
	public Subscription getSubscription(String address) {
		return subscriptions.get(address);
	}
	
	public Collection<Subscription> getSubscriptions() {
		return subscriptions.values();
	}

	public MailAccount getImapAccount() {
		return imapAccount;
	}

	public void setImapAccount(MailAccount imapAccount) {
		this.imapAccount = imapAccount;
	}

	public MailAccount getSmtpAccount() {
		return smtpAccount;
	}

	public void setSmtpAccount(MailAccount smtpAccount) {
		this.smtpAccount = smtpAccount;
	}

}
