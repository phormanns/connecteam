package de.jalin.connecteam.mail.loop;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.jalin.connecteam.etc.Config;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.etc.Mailinglist;
import de.jalin.connecteam.etc.Space;

public class Loop {

	private static Logger log = Logger.getLogger("Loop.class"); 
	
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
		final Fetchmail fetchmail = new Fetchmail();
		
		isRunning = true;
		log.info("start loop");
		while (isRunning) {
			for (Mailinglist ml : lists) {
				fetchmail.fetch(ml);
			}
			sleep(1);
		}
	}

	public void sleep(int secs) {
		try { Thread.sleep(1000L * secs); } catch (InterruptedException e) { }
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
