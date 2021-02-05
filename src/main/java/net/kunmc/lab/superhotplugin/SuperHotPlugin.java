package net.kunmc.lab.superhotplugin;

import net.kunmc.lab.superhotplugin.event.SuperHotPluginConstantEvent;
import net.kunmc.lab.superhotplugin.event.SuperHotPluginEventListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SuperHotPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		// Plugin startup logic
		this.getLogger().info("Enabled SUPER HOT Plugin!");
		this.register();
		new SuperHotPluginConstantEvent(this).runTaskTimer(this, 0, 0);
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
