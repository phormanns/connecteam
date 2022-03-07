package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;

import de.jalin.connecteam.data.DataAccess;
import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.Config;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;

public class Loop implements Runnable {

	private static Logger log = Logger.getLogger("Loop.class"); 
	
	private final Config config;
	
	private boolean running;
	private Collection<Topic> topics;
	private DataAccess dataAccess;

	private Fetchmail fetchmail;
	
	public Loop(final Config conf) throws CxException {
		config = conf;
	}
	
	public void init(final Connection dbConnection) throws CxException {
		dataAccess = new DataAccess(dbConnection);
		topics = dataAccess.listTopics();
		fetchmail = new Fetchmail(dbConnection, config.getDatadir());
		setRunning(false);
	}

	public void sleep(int secs) {
		try { Thread.sleep(1000L * secs); } catch (InterruptedException e) { }
	}

	public void stop() throws IOException {
		System.out.println("stop");
		setRunning(false);
		dataAccess = null;
	}
	
	public synchronized boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		setRunning(true);
		log.info("start loop");
		while (isRunning()) {
			try {
				for (Topic topic : topics) {
					final Topic loadedTopic = dataAccess.loadTopic(topic.getAddress());
					fetchmail.fetchAll(loadedTopic);
				}
			} catch (CxException e) {
				log.error(e);
			}
			sleep(1);
		}
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
	}

}
