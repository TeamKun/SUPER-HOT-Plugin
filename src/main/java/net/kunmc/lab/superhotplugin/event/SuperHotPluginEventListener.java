package net.kunmc.lab.superhotplugin.event;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import net.kunmc.lab.superhotplugin.SuperHotPlugin;
import net.kunmc.lab.superhotplugin.helper.SuperHotPluginHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class SuperHotPluginEventListener implements Listener {
	private final SuperHotPlugin plugin;

	public SuperHotPluginEventListener(SuperHotPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (SuperHotPluginConstantEvent.kunMovementState == SuperHotPluginConstantEvent.KunMovementState.Stopping) {
			if (!SuperHotPluginHelper.isKun(event.getPlayer())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerShoot(PlayerLaunchProjectileEvent event) {
		if (SuperHotPluginConstantEvent.kunMovementState == SuperHotPluginConstantEvent.KunMovementState.Stopping) {
			SuperHotPluginHelper.freeze(event.getProjectile());
		}
	}

	@EventHandler
	public void onUseClock(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (event.getItem().getType().equals(Material.CLOCK)) {
			if (SuperHotPluginHelper.clockHolder == null) {
				SuperHotPluginHelper.clockHolder = player.getDisplayName();
				player.getWorld().getEntities().stream()
					.filter(e -> !SuperHotPluginHelper.isClockHolder(e))
					.forEach(SuperHotPluginHelper::freeze);
				player.sendMessage("時間が止まった！");
			} else if (SuperHotPluginHelper.clockHolder == player.getDisplayName()) {
				player.getWorld().getEntities().stream()
					.filter(e -> !SuperHotPluginHelper.isClockHolder(e))
					.forEach(SuperHotPluginHelper::release);
				SuperHotPluginHelper.clockHolder = null;
				player.sendMessage("時間が動き出した！");
			}
		}
	}
}
