package de.jalin.connecteam.mail.loop;

import java.util.Properties;

import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.Mailinglist;
import de.jalin.connecteam.mail.message.MailinglistMessage;
import de.jalin.connecteam.mail.message.MessageTransformer;
import jakarta.mail.Flags.Flag;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class Fetchmail {

	private static Logger log = Logger.getLogger("Fetchmail.class"); 

	public void fetch(Mailinglist ml) {
		try {
			final Session session = Session.getDefaultInstance(new Properties());
			final Store store = session.getStore("imaps");
			store.connect(ml.getImapHost(), ml.getImapLogin(), ml.getImapPasswd());
			Folder defaultFolder = store.getDefaultFolder();
			final Folder[] children = defaultFolder.listSubscribed();
			for (Folder child : children) {
				child.open(Folder.READ_WRITE);
				final int type = child.getType();
				if ((type & Folder.HOLDS_MESSAGES) > 0) {
					final Message[] messages = child.getMessages();
					for (int idx = 1; idx <= messages.length; idx++) {
						Message message = child.getMessage(idx);
						boolean isSeen = message.isSet(Flag.SEEN);
						if (!isSeen) {
							MessageTransformer messageTransformer = new MessageTransformer(ml);
							MailinglistMessage eMailMessage = messageTransformer.parseMessage(message);
							log.info("message from: " + eMailMessage.getFromAddress());
							
							
							message.setFlag(Flag.SEEN, true);
						}
						
						
					}
				}
				
				child.close();
			}
			
			store.close();
		} catch (MessagingException e) {
			
		}
	}

}
