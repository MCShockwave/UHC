package net.mcshockwave.UHC.db;

import net.mcshockwave.UHC.UltraHC;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public enum ConfigFile {

	Bans(
		"bans.yml"),
	HOF(
		"halloffame.yml"),
	Default(
		"config.yml");

	public String				name;

	private FileConfiguration	config	= null;
	private File				file	= null;

	private ConfigFile(String name) {
		this.name = name;

		saveDefaults();
	}

	@SuppressWarnings("deprecation")
	public void reload() {
		if (file == null) {
			file = new File(UltraHC.ins.getDataFolder(), name);
		}
		config = YamlConfiguration.loadConfiguration(file);

		// Look for defaults in the jar
		InputStream defConfigStream = UltraHC.ins.getResource(name);
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}

	public FileConfiguration get() {
		if (config == null) {
			reload();
		}
		return config;
	}

	public void update() {
		save();
		reload();
	}

	public void save() {
		if (config == null || file == null) {
			return;
		}
		try {
			get().save(file);
		} catch (IOException ex) {
			UltraHC.ins.getLogger().log(Level.SEVERE, "Could not save config to " + file, ex);
		}
	}

	public void saveDefaults() {
		if (file == null) {
			file = new File(UltraHC.ins.getDataFolder(), name);
		}
		if (!file.exists()) {
			UltraHC.ins.saveResource(name, false);
		}
	}

}
