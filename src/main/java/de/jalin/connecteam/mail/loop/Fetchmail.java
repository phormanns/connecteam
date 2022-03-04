package de.jalin.connecteam.mail.loop;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.jalin.connecteam.data.DataAccess;
import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.DataDir;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.mail.message.MailinglistMessage;
import de.jalin.connecteam.mail.message.MessageClassifier;
import de.jalin.connecteam.mail.message.MessageParser;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class Fetchmail {

	private static Logger log = Logger.getLogger("Fetchmail.class"); 

	private final Connection dbConnection;
	private final DataDir datadir;
	
	private List<MailinglistMessage> sendQueue = null;

	public Fetchmail(final Connection dbConnection, final DataDir datadir) {
		this.dbConnection = dbConnection;
		this.datadir = datadir;
	}
	
	public void fetchAll(final Topic topic) {
		try {
			final DataAccess dataAccess = new DataAccess(dbConnection);
			sendQueue = new ArrayList<>();
			final MessageParser messageParser = new MessageParser(topic, datadir);
			final Session session = Session.getInstance(new Properties());
			final Store store = session.getStore("imaps");
			store.connect(topic.getImapAccount().getHost(), topic.getImapAccount().getLogin(), topic.getImapAccount().getPasswd());
			final Folder defaultFolder = store.getDefaultFolder();
			final Folder[] children = defaultFolder.listSubscribed();
			for (Folder child : children) {
				child.open(Folder.READ_WRITE);
				final int type = child.getType();
				if ((type & Folder.HOLDS_MESSAGES) > 0) {
					final Message[] messages = child.getMessages();
					for (int idx = 1; idx <= messages.length; idx++) {
						try {
							Message message = child.getMessage(idx);
							boolean isSeen = message.isSet(Flag.SEEN);
							if (!isSeen) {
								final MailinglistMessage eMailMessage = messageParser.parse(message);
								final MessageClassifier messageClassifier = new MessageClassifier(topic, eMailMessage);
								if (messageClassifier.isAccepted()) { 
									sendQueue.add(eMailMessage);
									dataAccess.storeMessage(eMailMessage, topic.getId());
								} else {
									log.info("message from " + eMailMessage.getOriginalFrom() + " rejected.");
								}
								message.setFlag(Flag.SEEN, true);
							}
						} catch (CxException e) {
							log.error(e);
						}
					}
				}
				child.close();
			}
			store.close();
			if (!sendQueue.isEmpty()) {
				final Sendmail sendmail = new Sendmail(topic);
				sendmail.sendAll(sendQueue);
				sendQueue.clear();
			}
		} catch (MessagingException e) {
			log.error(e);
		}
	}

}
