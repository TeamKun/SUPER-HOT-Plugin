package net.kunmc.lab.superhotplugin.config;

import net.kunmc.lab.superhotplugin.SuperHotPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class SuperHotConfig {
	public static boolean superHotEnabled;
	public static String timeFreezer;

	public static void load(boolean isReload) {
		SuperHotPlugin plugin = SuperHotPlugin.getPlugin();

		plugin.saveDefaultConfig();

		if (isReload) {
			plugin.reloadConfig();
		}

		FileConfiguration config = plugin.getConfig();

		superHotEnabled = config.getBoolean("superHotEnabled");
		timeFreezer = config.getString("timeFreezer");
	}
}
