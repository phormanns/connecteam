package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.util.Properties;

import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.Mailinglist;
import de.jalin.connecteam.mail.message.MailinglistMessage;
import de.jalin.connecteam.mail.message.MessageParser;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

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
							MessageParser messageTransformer = new MessageParser(ml);
							MailinglistMessage eMailMessage = messageTransformer.parseMessage(message);
							log.info("message from: " + eMailMessage.getFromAddress());
							
							
							message.setFlag(Flag.SEEN, true);
						}
						
						
					}
				}
				
				child.close();
			}
			
			store.close();
		} catch (MessagingException | IOException e) {
			
		}
	}

}
