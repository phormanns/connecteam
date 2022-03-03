package de.jalin.connecteam.mail.loop;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.mail.message.MailinglistMessage;
import de.jalin.connecteam.mail.message.MessageParser;

public class Fetchmail {

	private static Logger log = Logger.getLogger("Fetchmail.class"); 

	private List<MailinglistMessage> sendQueue = null;
	
	public void fetch(Topic topic) {
		try {
			sendQueue = new ArrayList<>();
			final MessageParser messageTransformer = new MessageParser(topic);
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
								final MailinglistMessage eMailMessage = messageTransformer.parseMessage(message);
								sendQueue.add(eMailMessage);
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
				sendQueue.forEach(new Consumer<MailinglistMessage>() {
					@Override
					public void accept(MailinglistMessage msg) {
						sendmail.send(msg);
					}
				});
				sendQueue.clear();
			}
		} catch (MessagingException e) {
			
		}
	}

}
