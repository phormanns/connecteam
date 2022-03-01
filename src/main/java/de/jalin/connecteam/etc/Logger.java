package de.jalin.connecteam.etc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Logger {

	private static Map<String, Logger> loggers = new HashMap<>();
	
	public synchronized static Logger getLogger(String name) {
		if (!loggers.containsKey(name)) {
			loggers.put(name, new Logger(name));
		}
		return loggers.get(name);
	}
	
	private DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	private String name;

	private Logger(String name) { 
		this.name = name;
	}
	
	public synchronized void info(String info) {
		log("INFO", info);
	}
	
	public synchronized void warn(String info) {
		log("WARN", info);
	}
	
	public synchronized void error(String info) {
		log("ERR ", info);
	}

	public synchronized void error(Throwable exc) {
		log("ERR ", exc.getMessage());
		System.err.println(df.format(new Date()) + " ERR  " + exc.getMessage());
		exc.printStackTrace(System.err);
	}

	private void log (String level, String text) {
		System.out.println(df.format(new Date()) + " " + level + " [" + name + "] " + text);
	}
	
}
