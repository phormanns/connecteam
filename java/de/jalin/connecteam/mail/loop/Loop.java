package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import de.jalin.connecteam.etc.Config;
import de.jalin.connecteam.etc.Mailinglist;
import de.jalin.connecteam.etc.Space;
import de.jalin.connecteam.mail.message.MailinglistMessage;
import de.jalin.connecteam.mail.message.MessageTransformer;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Flags.Flag;

public class Loop {

	private boolean isRunning;
	private final List<Mailinglist> lists;
	
	public Loop(Config conf) { 
		final List<Space> spaces = conf.getSpaces();
		lists = new ArrayList<>();
		for (Space s : spaces) {
			s.getLists().forEach(new Consumer<Mailinglist>() {
				@Override
				public void accept(Mailinglist ml) {
					lists.add(ml);
				}
			});
		}
	}
	
	public void start() {
		isRunning = true;
		while (isRunning) {
			
			System.out.print(".");
			
			try {
				for (Mailinglist ml : lists) {
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
									System.out.println(eMailMessage.getFromAddress());
									
									
									message.setFlag(Flag.SEEN, true);
								}
								
								
							}
						}
						
						child.close();
					}
					
					store.close();
				}
				Thread.sleep(500L);
			} catch (InterruptedException | MessagingException e) {
				System.out.println("stop");
				isRunning = false;
			}
		}
	}

	public void stop() {
		System.out.println("stop");
		isRunning = false;
	}
	
	public static void main(String[] args) {
		try {
			Config conf = Config.load(Paths.get("conf/config.yaml"));
			Loop loop = new Loop(conf);
			loop.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
