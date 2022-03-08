package de.jalin.connecteam.etc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class Config {

	private static Logger log = Logger.getLogger("Config.class");
	
	private Database database;
	private DataDir datadir;
	
	public Config() {
		database = new Database();
		setDatadir(new DataDir());
	}
	
	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public DataDir getDatadir() {
		return datadir;
	}

	public void setDatadir(DataDir datadir) {
		this.datadir = datadir;
	}

	public void dump(Path configPath) throws CxException {
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setPrettyFlow(true);        
		final Yaml yml = new Yaml(options);
		try {
			yml.dump(this, new FileWriter(configPath.toFile()));
		} catch (IOException e) {
			log.error(e);
			throw new CxException(e);
		}
	}

	public static Config load(Path configPath) throws CxException {
		log.info("load config from " + configPath.toString());
		final Constructor constructor = new Constructor(Config.class);
		final Yaml yml = new Yaml(constructor);
		Config config;
		try {
			config = yml.load(new FileInputStream(configPath.toFile()));
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new CxException(e);
		}
		log.info("config successfully loaded");
		return config;
	}

}
