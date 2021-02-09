package net.kunmc.lab.superhotplugin;

import net.kunmc.lab.superhotplugin.config.SuperHotConfig;
import net.kunmc.lab.superhotplugin.event.SuperHotPluginConstantEvent;
import net.kunmc.lab.superhotplugin.event.SuperHotPluginEventListener;
import net.kunmc.lab.superhotplugin.helper.SuperHotPluginHelper;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperHotPlugin extends JavaPlugin {
	public static SuperHotPlugin plugin;

	public static SuperHotPlugin getPlugin() {
		return plugin;
	}

	@Override
	public void onEnable() {
		// Plugin startup logic
		this.getLogger().info("Enabled SUPER HOT Plugin!");
		this.register();
		plugin = this;
		new SuperHotPluginConstantEvent(this).runTaskTimer(plugin, 0, 0);
		SuperHotConfig.load(false);
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
		this.getLogger().info("Disabled SUPER HOT Plugin!");
	}

	private void register() {
		this.getServer().getPluginManager().registerEvents(new SuperHotPluginEventListener(this), this);
	}
}
