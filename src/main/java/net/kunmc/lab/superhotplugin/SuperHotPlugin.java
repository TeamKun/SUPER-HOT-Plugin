package net.kunmc.lab.superhotplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class SuperHotPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		// Plugin startup logic
		this.getLogger().info("Enabled SUPER HOT Plugin!");
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
		getLogger().info("Disabled SUPER HOT Plugin!あ");
	}

	private void register() {
		this.getServer().getPluginManager().registerEvent(, this);
	}
}
