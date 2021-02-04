package net.kunmc.lab.superhotplugin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class SuperHotPluginEventListener implements Listener {
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getName().equalsIgnoreCase("Dev")) {
			player.sendMessage("Moving!");
		}
	}
}
