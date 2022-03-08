package de.jalin.connecteam.etc;

import java.io.FileInputStream;
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

	public void dump(Path configPath) throws IOException {
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setPrettyFlow(true);        
		final Yaml yml = new Yaml(options);
		yml.dump(this, new FileWriter(configPath.toFile()));
	}

	public static Config load(Path configPath) throws IOException {
		log.info("load config from " + configPath.toString());
		final Constructor constructor = new Constructor(Config.class);
		final Yaml yml = new Yaml(constructor);
		return yml.load(new FileInputStream(configPath.toFile()));
	}

}
