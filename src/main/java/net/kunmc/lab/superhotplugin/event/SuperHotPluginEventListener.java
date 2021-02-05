package net.kunmc.lab.superhotplugin.event;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import net.kunmc.lab.superhotplugin.SuperHotPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SuperHotPluginEventListener implements Listener {
	private static SuperHotPlugin plugin;
	private static boolean isKunMoving;
	private static Double lastKunXPos = 0D;
	private static Double lastKunZPos = 0D;
	private static Map<UUID, Vector> projectileVelocity = new HashMap<>();
	private static Map<UUID, Vector> fireballDirection = new HashMap<>();
	private static String clockHolder = null;
	private static int counter = 0;
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
				player.getWorld().getEntities().stream()
					.filter(e -> !isKun(e))
					.forEach(SuperHotPluginEventListener::release);
				player.sendMessage("時間が動いた");
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!isKunMovingWorld(player)) {
							player.getWorld().getEntities().stream()
								.filter(e -> !isKun(e))
								.forEach(SuperHotPluginEventListener::freeze);
							player.sendMessage("時間が止まった");
							isKunMoving = false;
							this.cancel();
						}
					}
				}.runTaskTimer(plugin, 0, 0);
				isKunMoving =true;
			}
		}
		else {
			if (isKunMoving) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerShoot(PlayerLaunchProjectileEvent event) {
		event.getPlayer().sendMessage(Boolean.toString(isKunMoving));
		if (!isKunMoving || clockHolder != null) {
			freeze(event.getProjectile());
		}
	}

	@EventHandler
	public void onUseClock(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (event.getItem().getType().equals(Material.CLOCK)) {
			if (clockHolder == null) {
				clockHolder = player.getDisplayName();
				player.getWorld().getEntities().stream()
					.filter(e -> !isClockHolder(e))
					.forEach(SuperHotPluginEventListener::freeze);
			} else if (clockHolder == player.getDisplayName()) {
				player.getWorld().getEntities().stream()
					.filter(e -> !isClockHolder(e))
					.forEach(SuperHotPluginEventListener::release);
				clockHolder = null;
			}
		}
	}

	private static void freeze(Entity entity) {
		entity.setGravity(false);
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			living.setAI(false);
		} else if (entity instanceof Fireball) {
			Fireball fireball = (Fireball) entity;
			fireballDirection.put(fireball.getUniqueId(), fireball.getVelocity());
			fireball.setVelocity(fireball.getDirection().zero());
			/*new BukkitRunnable() {
				@Override
				public void run() {
					if (counter % 2 == 0) {
						fireball.setDirection(new Vector(0, 1, 0));
					}
					else {
						fireball.setDirection(new Vector(0, -1, 0));
					}
					if (isKunMoving && clockHolder == null) {
						counter = 0;
						fireball.setDirection(fireballDirection.get(fireball.getUniqueId()));
						this.cancel();
					}
					counter ++;
				}
			}.runTaskTimer(plugin, 0, 0);*/
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			projectileVelocity.put(projectile.getUniqueId(), projectile.getVelocity());
			projectile.setVelocity(new Vector());
		}
	}

	private static void release(Entity entity) {
		entity.setGravity(true);
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			living.setAI(true);
		} else if (entity instanceof Fireball) {
			Fireball fireball = (Fireball) entity;
			try {
				fireball.setVelocity(fireballDirection.get(fireball.getUniqueId()));
			} catch (IllegalArgumentException e) {
				System.out.println(e.toString());
			}
		} else if (entity instanceof Projectile) {
			Projectile projectile = (Projectile) entity;
			try {
				projectile.setVelocity(projectileVelocity.get(projectile.getUniqueId()));
			} catch (IllegalArgumentException e) {
				System.out.println(e.toString());
			}
		}
	}

	private static boolean isKun(Entity entity) {
		return entity.getName().equalsIgnoreCase("Shojo_Virgi");
	}

	private static boolean isClockHolder(Entity entity) {
		return entity.getName().equalsIgnoreCase(clockHolder);
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
