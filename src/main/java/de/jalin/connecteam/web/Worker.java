package de.jalin.connecteam.web;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import de.jalin.connecteam.etc.Config;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.mail.loop.Loop;

public class Worker implements ServletContextListener {

	private static Logger log = Logger.getLogger("Worker.class");
	
	@Resource(name = "jdbc/connecteam")
	private DataSource dataSource;
	
	private Connection dbConnection;
	private Loop loop;

	@Override
	public void contextInitialized(ServletContextEvent initializedEvent) {
		log.info("start worker thread");
		try {
			final Config conf = Config.load(Paths.get("./conf/config.yaml"));
			loop = new Loop(conf);
			dbConnection = dataSource.getConnection();
			loop.init(dbConnection);
			final Thread thread = new Thread(loop);
			thread.start();
		} catch (IOException | CxException | SQLException e) {
			log.error(e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent destroyedEvent) {
		try {
			loop.setRunning(false);
			dbConnection.close();
		} catch (SQLException e) {
			log.error(e);
		}
		log.info("stop worker thread");
	}
}
