package net.kunmc.lab.superhotplugin.event;

import net.kunmc.lab.superhotplugin.SuperHotPlugin;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SuperHotPluginEventListener implements Listener {
	private final SuperHotPlugin plugin;
	private boolean isKunMoving;
	private static Double lastKunXPos = 0D;
	private static Double lastKunZPos = 0D;
	private static final UUID FROZEN_ID = UUID.randomUUID();
	private static final AttributeModifier FROZEN = new AttributeModifier(FROZEN_ID, "Freeze entity movement", -1.0D, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

	public SuperHotPluginEventListener(SuperHotPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (isKun(player)) {
			if (isKunMovingWorld(player) && !isKunMoving) {
				player.sendMessage("tr");
				player.getWorld().getLivingEntities().stream()
					.filter(l -> !isKun(l))
					.forEach(SuperHotPluginEventListener::release);
				player.sendMessage("時間が動いた");
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!isKunMovingWorld(player)) {
							player.getWorld().getLivingEntities().stream()
								.filter(l -> !isKun(l))
								.forEach(SuperHotPluginEventListener::freeze);
							player.sendMessage("時間が止まった");
							isKunMoving = false;
							this.cancel();
						}
					}
				}.runTaskTimer(plugin, 0, 1);
				isKunMoving =true;
			}
		}
		else {
			if (isKunMoving) {
				event.setCancelled(true);
			}
		}
	}

	private static void freeze(LivingEntity living) {
		living.setAI(false);
		living.setGravity(false);
	}

	private static void release(LivingEntity living) {
		living.setAI(true);
		living.setGravity(true);
	}

	private static boolean isKun(LivingEntity living) {
		return living.getName().equalsIgnoreCase("Shojo_Virgim");
	}

	private static boolean isPlayerMovingWorld(PlayerMoveEvent player) {
		return player.getFrom().getX() != player.getTo().getX()
			|| player.getFrom().getZ() != player.getTo().getZ();
	}

	private static boolean isKunMovingWorld(Player player) {
		Double currentXPos = player.getLocation().getX();
		Double currentZPos = player.getLocation().getZ();
		int diffX = currentXPos.compareTo(lastKunXPos);
		int diffZ = currentXPos.compareTo(lastKunZPos);
		lastKunXPos = currentXPos;
		lastKunZPos = currentZPos;
		return diffX == 1 || diffZ == 1;
	}
}
