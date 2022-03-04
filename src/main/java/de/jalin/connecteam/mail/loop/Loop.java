package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import de.jalin.connecteam.data.DataAccess;
import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.Config;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Database;
import de.jalin.connecteam.etc.Logger;

public class Loop {

	private static Logger log = Logger.getLogger("Loop.class"); 
	
	private final Config config;
	
	private boolean isRunning;
	private Collection<Topic> topics;
	private DataAccess dataAccess;
	
	public Loop(final Config conf) throws CxException {
		config = conf;
	}
	
	public void start(final Connection dbConnection) throws CxException {
		dataAccess = new DataAccess(dbConnection);
		topics = dataAccess.listTopics();
		final Fetchmail fetchmail = new Fetchmail(config.getDatadir());
		isRunning = true;
		log.info("start loop");
		while (isRunning) {
			for (Topic topic : topics) {
				final Topic loadedTopic = dataAccess.loadTopic(topic.getAddress());
				fetchmail.fetch(loadedTopic);
			}
			sleep(1);
		}
	}

	public void sleep(int secs) {
		try { Thread.sleep(1000L * secs); } catch (InterruptedException e) { }
	}

	public void stop() throws IOException {
		System.out.println("stop");
		isRunning = false;
		dataAccess.close();
		dataAccess = null;
	}
	
	public static void main(String[] args) {
		try {
			Config conf = Config.load(Paths.get("conf/config.yaml"));
			Database database = conf.getDatabase();
			
			String url = "jdbc:postgresql://" + database.getHost() + ":" + database.getPort() + "/" + database.getName();
			final Properties props = new Properties();
			props.setProperty("user", database.getUser());
			props.setProperty("password", database.getPassword());
			final Connection conn = DriverManager.getConnection(url, props);			
			final Loop loop = new Loop(conf);
			loop.start(conn);
		} catch (IOException | CxException | SQLException e) {
			log.error(e);
		}
	}
	
}
