package de.jalin.connecteam.etc;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class Config {

	private java.util.List<Space> spaces;
	
	public Config() {
		spaces = new ArrayList<>();
	}
	
	public List<Space> getSpaces() {
		return spaces;
	}

	public void setSpaces(java.util.List<Space> spaces) {
		this.spaces = spaces;
	}
	
	public void dump(Path configPath) throws IOException {
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setPrettyFlow(true);        
		final Yaml yml = new Yaml(options);
		yml.dump(this, new FileWriter(configPath.toFile()));
	}

	public static Config load(Path configPath) throws IOException {
		final Constructor constructor = new Constructor(Config.class);
		final Yaml yml = new Yaml(constructor);
		return yml.load(new FileInputStream(configPath.toFile()));
	}

	public static void main(String[] args) {
		final Path sampleConfigPath = Paths.get("./conf/config.sample.yaml");
		final Path tempConfigPath = Paths.get("./conf/config.temp.yaml");
		try {
			final Config config = Config.load(sampleConfigPath);
			config.dump(tempConfigPath);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
